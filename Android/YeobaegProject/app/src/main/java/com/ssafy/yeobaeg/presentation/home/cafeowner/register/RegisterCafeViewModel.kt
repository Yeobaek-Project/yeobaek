// RegisterCafeViewModel.kt
package com.ssafy.yeobaeg.presentation.home.cafeowner.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.cafe.CafeCategory
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe
import com.ssafy.yeobaeg.domain.model.storage.PresignedUrl
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.domain.repository.StorageRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.toList
import kotlin.collections.toMutableSet

const val STEP1 = 1
const val STEP2 = 2
const val STEP_ADDRESS = 0
const val TOTAL_STEPS = 2

@HiltViewModel
class RegisterCafeViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val storageRepository: StorageRepository,
    private val navigator: Navigator,
    private val snackBarController: SnackBarController
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterCafeState())
    val uiState: StateFlow<RegisterCafeState> = _uiState.asStateFlow()

    private fun update(block: RegisterCafeState.() -> RegisterCafeState) {
        _uiState.value = _uiState.value.block()
    }
    
    fun updateName(value: String) {
        if (value.isNotEmpty() && value.length < 2) update { copy(nameError = "매장명은 2~20자 이내여야 합니다.")}
        else update { copy(nameError = null)}
        update { copy(name = value) }
    }
    fun updateDescription(value: String) = update { copy(description = value.take(500)) }
    fun updatePhone(value: String) = update { copy(phone = value) }
    fun updateOpenTime(value: String) = update { copy(openTime = value) }
    fun updateCloseTime(value: String) = update { copy(closeTime = value) }

    fun updateAddress(value: String) = update { copy(address = value) }

    fun openAddressPicker() = update { copy(step = STEP_ADDRESS) }
    fun closeAddressPicker() = update { copy(step = STEP1) }



    fun next() {
        if (_uiState.value.step == TOTAL_STEPS) {
            registerCafe()
        }
        update { copy(step = if (step == STEP1) STEP2 else step) }
    }

    fun prev() = update { copy(step = if (step == STEP2) STEP1 else step) }

    fun toggleStyle(category: CafeCategory) = update {
        val set = selectedStyles.toMutableSet()
        if (category in set) set.remove(category) else if (set.size < 3) set.add(category)
        copy(selectedStyles = set)
    }

    fun selectVisitorsBand(band: VisitorsBand) = update { copy(visitorsBand = band) }

    fun addImage(addImageFiles: List<File>) {
        update { if (imageFiles.size >= 10) this else copy(imageFiles = imageFiles + addImageFiles) }
        uploadImage(addImageFiles)
    }

    fun removeImage(image: PresignedUrl) {
        _uiState.update { it.copy(isLoading = false) }

        viewModelScope.launch {
            storageRepository.deleteImage(image.fileKey).onSuccess {
                update { copy(uploadedImages = uploadedImages - image) }
            }.onFailure {
                Log.d("registerCafeViewModel removeImage", "실패")
            }
        }


    }

    fun getCafeCategories() {
        viewModelScope.launch {
            cafeRepository.getCafeCategories().onSuccess { categories ->
                _uiState.update { it.copy(cafeCategories = categories) }
            }
        }
    }

    private fun uploadImage(addImageFiles: List<File>) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            storageRepository.createPresignedUrl(folder = "CAFE", count = addImageFiles.size)
                .onSuccess { data ->
                    data.forEachIndexed { index, presignedUrl ->
                        launch {
                            storageRepository.uploadWithPresignedPut(
                                file = addImageFiles[index],
                                presignedUrl = presignedUrl.uploadUrl
                            ).onSuccess {
                                _uiState.update {
                                    it.copy(
                                        uploadedImages = _uiState.value.uploadedImages + presignedUrl,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                }
                .onFailure {
                    Log.d("http s3 viewModel error", it.message!!)
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun navigateToCafeOwnerHome() = viewModelScope.launch {
        navigator.navigateBack()
    }

    private fun registerCafe() = viewModelScope.launch {
        val s = _uiState.value
        val cafe = RegisterCafe(
            cafeName = s.name,
            address = s.address,
            description = s.description,
            contact = s.phone,
            businessHoursStart = s.openTime,
            businessHoursEnd = s.closeTime,
            categoryIds = s.selectedStyles.map { it.categoryId },
            dailyVisitors = requireNotNull(s.visitorsBand),
            images = s.uploadedImages.map { it.fileKey }
        )

        cafeRepository.registerCafe(cafe)
            .onSuccess {
                snackBarController.showSuccess("카페 등록 완료")
                navigateToCafeOwnerHome()
            }
            .onFailure {
                snackBarController.showError(it.message ?: "오류")
                Log.d("RegisterCafeViewModel http", "카페 등록 실패: ${it.message}")
            }
    }
}

data class RegisterCafeState(
    val isLoading: Boolean = false,
    val step: Int = STEP1,
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val phone: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val imageFiles: List<File> = emptyList(),
    val uploadedImages: List<PresignedUrl> = emptyList(),
    val selectedStyles: Set<CafeCategory> = emptySet(),
    val cafeCategories: List<CafeCategory> = emptyList(),
    val visitorsBand: VisitorsBand? = null,
    val nameError: String? = null
) {
    val isStep1Valid
        get() =
            name.isNotBlank() && address.isNotBlank() && phone.isNotBlank() &&
                    openTime.isNotBlank() && closeTime.isNotBlank() && imageFiles.size in 3..10
    val isStep2Valid
        get() =
            selectedStyles.isNotEmpty() && selectedStyles.size <= 3 && visitorsBand != null
}

enum class VisitorsBand(val label: String) {
    UNDER_50("50명 이하"), V50_100("51 ~ 100명"), V100_200("101 ~ 200명"),
    V200_300("201 ~ 300명"), V300_500("301 ~ 500명"), OVER_500("500명 초과");
}
