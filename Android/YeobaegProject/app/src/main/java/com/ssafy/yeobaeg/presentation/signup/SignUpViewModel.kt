package com.ssafy.yeobaeg.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.Gender
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignUpUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val userRole: UserRole? = null,
    val birthDate: String = "",
    val gender: Gender? = null,
    val nickname: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    // 실시간 에러 메시지
    val emailError: String? = null,
    val passwordError: String? = null,
    val passwordConfirmError: String? = null,
    val birthDateError: String? = null,
    val nicknameError: String? = null,
    // 이메일 중복 확인 상태
    val isEmailChecking: Boolean = false,
    val isEmailAvailable: Boolean? = null,
    val emailCheckMessage: String? = null
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authRepository: AuthRepository,
    private val authSessionManager: AuthSessionManager,
    private val snackbarController: SnackBarController
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        val errorMessage = if (email.isNotBlank()) {
            ValidationUtils.getEmailErrorMessage(email)
        } else null

        _uiState.update {
            it.copy(
                email = email,
                emailError = errorMessage,
                // 이메일이 변경되면 중복 확인 상태 초기화
                isEmailAvailable = null,
                emailCheckMessage = null
            )
        }
    }

    fun checkEmailAvailability() {
        viewModelScope.launch {
            val email = _uiState.value.email

            // 이메일 형식 검증
            val emailError = ValidationUtils.getEmailErrorMessage(email)
            if (emailError != null) {
                _uiState.update {
                    it.copy(
                        emailError = emailError,
                        isEmailAvailable = null,
                        emailCheckMessage = null
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isEmailChecking = true) }

            val result = authRepository.checkEmailAvailability(email)

            result.fold(
                onSuccess = { available ->
                    _uiState.update {
                        it.copy(
                            isEmailChecking = false,
                            isEmailAvailable = available,
                            emailCheckMessage = if (available) {
                                "사용 가능한 이메일입니다"
                            } else {
                                "이미 사용 중인 이메일입니다"
                            }
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isEmailChecking = false,
                            isEmailAvailable = null,
                            emailCheckMessage = exception.message ?: "이메일 확인에 실패했습니다"
                        )
                    }
                }
            )
        }
    }
    
    fun updatePassword(password: String) {
        val passwordErrorMessage = if (password.isNotBlank()) {
            ValidationUtils.getPasswordErrorMessage(password)
        } else null
        
        val passwordConfirmErrorMessage = if (_uiState.value.passwordConfirm.isNotBlank()) {
            ValidationUtils.getPasswordConfirmErrorMessage(password, _uiState.value.passwordConfirm)
        } else null
        
        _uiState.update { 
            it.copy(
                password = password,
                passwordError = passwordErrorMessage,
                passwordConfirmError = passwordConfirmErrorMessage
            )
        }
    }
    
    fun updatePasswordConfirm(passwordConfirm: String) {
        val errorMessage = if (passwordConfirm.isNotBlank()) {
            ValidationUtils.getPasswordConfirmErrorMessage(_uiState.value.password, passwordConfirm)
        } else null
        
        _uiState.update { 
            it.copy(
                passwordConfirm = passwordConfirm,
                passwordConfirmError = errorMessage
            )
        }
    }
    
    fun selectUserRole(role: UserRole) {
        _uiState.update { 
            it.copy(userRole = role)
        }
    }
    
    fun updateBirthDate(birthDate: String) {
        val errorMessage = if (birthDate.isNotBlank()) {
            ValidationUtils.getBirthDateErrorMessage(birthDate)
        } else null
        
        _uiState.update { 
            it.copy(
                birthDate = birthDate,
                birthDateError = errorMessage
            )
        }
    }
    
    fun selectGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }
    
    fun updateNickname(nickname: String) {
        val errorMessage = if (nickname.isNotBlank()) {
            ValidationUtils.getNicknameErrorMessage(nickname)
        } else null
        
        _uiState.update { 
            it.copy(
                nickname = nickname,
                nicknameError = errorMessage
            )
        }
    }

    fun onBack() {
        viewModelScope.launch {
            navigator.navigateBack()
        }
    }

    fun nextStep() {
        viewModelScope.launch {
            // 현재 단계 검증
            val isValid = when (_uiState.value.currentStep) {
                1 -> validateEmailPasswordStep()
                2 -> validateRoleSelectionStep()
                3 -> validateGenderAndAgeStep()
                4 -> validateNicknameStep()
                else -> false
            }
            
            if (!isValid) {
                return@launch
            }
            
            // 4단계가 마지막 단계이므로 회원가입 처리
            if (_uiState.value.currentStep == 4) {
                performSignUp()
                return@launch
            }
            
            _uiState.update { 
                it.copy(currentStep = it.currentStep + 1)
            }
        }
    }
    
    private fun validateEmailPasswordStep(): Boolean {
        val state = _uiState.value
        val emailError = ValidationUtils.getEmailErrorMessage(state.email)
        val passwordError = ValidationUtils.getPasswordErrorMessage(state.password)
        val passwordConfirmError = ValidationUtils.getPasswordConfirmErrorMessage(state.password, state.passwordConfirm)
        
        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                passwordConfirmError = passwordConfirmError
            )
        }
        
        return emailError == null && passwordError == null && passwordConfirmError == null
    }
    
    private fun validateRoleSelectionStep(): Boolean {
        return _uiState.value.userRole != null
    }
    
    private fun validateGenderAndAgeStep(): Boolean {
        val state = _uiState.value
        val birthDateError = ValidationUtils.getBirthDateErrorMessage(state.birthDate)
        
        _uiState.update {
            it.copy(birthDateError = birthDateError)
        }
        
        if (state.gender == null) {
            _uiState.update { it.copy(error = "성별을 선택해주세요") }
            return false
        }
        
        return birthDateError == null
    }
    
    private fun validateNicknameStep(): Boolean {
        val state = _uiState.value
        val nicknameError = ValidationUtils.getNicknameErrorMessage(state.nickname)
        
        _uiState.update {
            it.copy(nicknameError = nicknameError)
        }
        
        return nicknameError == null
    }
    
    private suspend fun performSignUp() {
        val state = _uiState.value
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        val userType = when (state.userRole) {
            UserRole.GENERAL -> "GENERAL"
            UserRole.CAFE_OWNER -> "CAFE_OWNER"
            UserRole.ARTIST -> "ARTIST"
            null -> {
                _uiState.update { it.copy(isLoading = false, error = "사용자 유형을 선택해주세요") }
                return
            }
        }
        
        val gender = when (state.gender) {
            Gender.MALE -> "MALE"
            Gender.FEMALE -> "FEMALE"
            null -> {
                _uiState.update { it.copy(isLoading = false, error = "성별을 선택해주세요") }
                return
            }
        }
        
        val result = authRepository.signUp(
            userType = userType,
            email = state.email,
            password = state.password,
            passwordConfirm = state.passwordConfirm,
            nickname = state.nickname,
            birthDate = state.birthDate,
            gender = gender
        )
        
        result.fold(
            onSuccess = { message ->
                _uiState.update {
                    it.copy(isLoading = false)
                }
                // 로그인 화면으로 이동
                navigator.navigateAndClearBackStack(FullScreen.Login)
                // 성공 스낵바 표시
                snackbarController.showSuccess("회원가입에 성공했습니다!")
            },
            onFailure = { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "회원가입에 실패했습니다"
                    )
                }
            }
        )
    }
    
    fun previousStep() {
        _uiState.update { state ->
            if (state.currentStep > 1) {
                state.copy(currentStep = state.currentStep - 1)
            } else {
                state
            }
        }
    }
    
    fun canProceedFromEmailPasswordStep(): Boolean {
        val state = _uiState.value
        return state.emailError == null &&
               state.passwordError == null &&
               state.passwordConfirmError == null &&
               state.email.isNotBlank() &&
               state.password.isNotBlank() &&
               state.passwordConfirm.isNotBlank() &&
               state.isEmailAvailable == true  // 이메일 중복 확인 필수
    }
    
    fun canProceedFromRoleSelectionStep(): Boolean {
        return _uiState.value.userRole != null
    }
    
    fun canProceedFromGenderAndAgeStep(): Boolean {
        val state = _uiState.value
        return state.birthDate.isNotBlank() && 
               state.birthDateError == null &&
               state.gender != null
    }
    
    fun canProceedFromNicknameStep(): Boolean {
        val state = _uiState.value
        return state.nickname.isNotBlank() && state.nicknameError == null
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
