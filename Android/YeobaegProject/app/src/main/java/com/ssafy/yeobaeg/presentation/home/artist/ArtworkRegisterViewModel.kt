package com.ssafy.yeobaeg.presentation.home.artist

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.artist.ArtworkRegister
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.StorageRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.uriToTempFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterArtworkUiState(
    val isLoading: Boolean = false,
    val currentStep: Int = 1,
    val imageUris: List<Uri> = emptyList(),
    val uploadedKeys: List<String> = emptyList(),
    val artworkData: ArtworkRegister = ArtworkRegister()
)

@HiltViewModel
class ArtworkRegisterViewModel @Inject constructor(
    private val artRepository: ArtRepository,
    private val storageRepository: StorageRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterArtworkUiState())
    val uiState: StateFlow<RegisterArtworkUiState> = _uiState.asStateFlow()

    val currentStep: StateFlow<Int> = _uiState
        .map { it.currentStep }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.currentStep)

    val imageUris: StateFlow<List<Uri>> = _uiState
        .map { it.imageUris }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.imageUris)

    fun nextStep() {
        if (_uiState.value.currentStep < 4) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
        }
    }

    fun prevStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    // 이미지 업로드 처리
    fun uploadArtworkImages(context: Context, onComplete: suspend () -> Unit = {}) {
        viewModelScope.launch {
            val uris = _uiState.value.imageUris
            if (uris.isEmpty()) return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)

            runCatching {
                // presigned url 리스트 요청
                val presignedList = storageRepository.createPresignedUrl("IMAGES", uris.size)
                    .getOrThrow()

                val uploadedKeys = mutableListOf<String>()

                uris.forEachIndexed { index, uri ->
                    val presigned = presignedList[index]
                    val file = uriToTempFile(context, uri)
                    storageRepository.uploadWithPresignedPut(file, presigned.uploadUrl).getOrThrow()
                    uploadedKeys.add(presigned.fileKey)
                }

                // 상태 업데이트
                _uiState.value = _uiState.value.copy(uploadedKeys = uploadedKeys)
                Log.d("ArtworkRegister", "Uploaded keys: $uploadedKeys")

                onComplete()
            }.onFailure {
                Log.e("ArtworkRegister", "이미지 업로드 실패: ${it.message}")
            }.also {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // 작품 등록
    fun registerArtwork(context: Context) {
        viewModelScope.launch {
            val data = _uiState.value.artworkData
            val uploadedKeys = _uiState.value.uploadedKeys

            // 이미지 업로드 필요 시 먼저 업로드
            if (uploadedKeys.isEmpty() && _uiState.value.imageUris.isNotEmpty()) {
                uploadArtworkImages(context) {
                    registerArtwork(context) // 업로드 완료 후 재호출
                }
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)

            val request = ArtworkRegister(
                title = data.title,
                description = data.description,
                price = data.price,
                category = data.category,
                widthCm = data.widthCm,
                heightCm = data.heightCm,
                weightKg = data.weightKg,
                productionYear = data.productionYear,
                images = uploadedKeys // 업로드된 fileKey 사용
            )

            runCatching {
                artRepository.registerArtwork(request)
            }.onSuccess {
                Log.d("ArtworkRegister", "Artwork registered successfully")
                navigateBack()
            }.onFailure {
                Log.e("ArtworkRegister", "Register failed: ${it.message}")
            }.also {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

    fun resetSteps() {
        _uiState.value = RegisterArtworkUiState()
    }

    fun addImages(context: Context, uris: List<Uri>) {
        viewModelScope.launch {
            if (uris.isEmpty()) return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)

            runCatching {
                // Presigned URL 여러 개 요청
                val presignedList = storageRepository
                    .createPresignedUrl("IMAGES", uris.size)
                    .getOrThrow()

                // 병렬 업로드 (각 업로드 완료 시마다 UI 갱신)
                uris.forEachIndexed { index, uri ->
                    val presigned = presignedList[index]
                    val file = uriToTempFile(context, uri)

                    launch {
                        runCatching {
                            storageRepository.uploadWithPresignedPut(file, presigned.uploadUrl).getOrThrow()

                            // 업로드 성공 시 개별 이미지 즉시 추가
                            val updatedUris = _uiState.value.imageUris.toMutableList()
                            val updatedKeys = _uiState.value.uploadedKeys.toMutableList()

                            updatedUris.add(uri)
                            updatedKeys.add(presigned.fileKey)

                            _uiState.value = _uiState.value.copy(
                                imageUris = updatedUris,
                                uploadedKeys = updatedKeys
                            )

                            Log.d("ArtworkRegister", "S3 업로드 성공: ${presigned.fileKey}")
                        }.onFailure {
                            Log.e("ArtworkRegister", "S3 업로드 실패: ${it.message}")
                        }
                    }
                }
            }.onFailure {
                Log.e("ArtworkRegister", "Presigned URL 요청 실패: ${it.message}")
            }.also {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }



    fun updateArtworkData(
        title: String? = null,
        description: String? = null,
        price: Int? = null,
        category: ArtworkCategory? = null,
        widthCm: Int? = null,
        heightCm: Int? = null,
        weightKg: Double? = null,
        productionYear: String? = null
    ) {
        val current = _uiState.value.artworkData
        _uiState.value = _uiState.value.copy(
            artworkData = current.copy(
                title = title ?: current.title,
                description = description ?: current.description,
                price = price ?: current.price,
                category = category ?: current.category,
                widthCm = widthCm ?: current.widthCm,
                heightCm = heightCm ?: current.heightCm,
                weightKg = weightKg ?: current.weightKg,
                productionYear = productionYear ?: current.productionYear
            )
        )
    }


    fun removeImage(uri: Uri) {
        val current = _uiState.value.imageUris.toMutableList()
        current.remove(uri)
        _uiState.value = _uiState.value.copy(imageUris = current)
    }

}
