package com.ssafy.yeobaeg.presentation.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.domain.repository.StorageRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.uriToTempFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val storageRepository: StorageRepository,
    private val navigator: Navigator,
    private val authSessionManager: AuthSessionManager,
    ) : ViewModel() {

    fun updateProfile(newNickname: String, newImage: String?, artistDes: String?) =
        viewModelScope.launch {
            viewModelScope.launch {
                repository.updateProfile(
                    nickname = newNickname,
                    profileImageUrl = newImage,
                    introduction = artistDes,
                ).onSuccess {
                    navigator.navigateBack() // 성공 시 이동
                }.onFailure { e ->
                    Log.e("ProfileUpdate", "프로필 수정 실패", e)
                }
            }
        }

    fun uploadProfileImage(context: Context, imageUri: Uri, onSuccess: (String) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                // 1. Uri → File 변환
                val file = uriToTempFile(context, imageUri)

                // 2. Presigned URL 요청
                val presignedList = storageRepository.createPresignedUrl("IMAGES", 1)
                    .getOrThrow()
                val presigned = presignedList.first()

                // 3. S3에 이미지 업로드
                storageRepository.uploadWithPresignedPut(file, presigned.uploadUrl).getOrThrow()

                // 4. fileKey를 onSuccess로 전달
                onSuccess(presigned.fileKey)

            } catch (e: Exception) {
                Log.e("ProfileEdit", "이미지 업로드 중 오류: ${e.message}")

                onError(e)
            }
        }
    }

    fun onSaveProfile(
        context: Context,
        nickname: String,
        introduction: String?,
        imageUri: Uri?,
    ) {
        viewModelScope.launch {
            if (imageUri != null) {
                // 새 이미지 업로드 → 성공 시 프로필 업데이트
                uploadProfileImage(
                    context = context,
                    imageUri = imageUri,
                    onSuccess = { fileKey ->
                        updateProfile(
                            newNickname = nickname,
                            newImage = fileKey,
                            artistDes =introduction
                        )
                    },
                    onError = { e -> Log.e("ProfileEdit", "이미지 업로드 실패: ${e.message}") }
                )
            } else {
                // 새 이미지 선택 안 했으면 기존 이미지 유지
                updateProfile(
                    newNickname = nickname,
                    newImage = null,
                    artistDes =introduction
                )
            }
        }
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}
