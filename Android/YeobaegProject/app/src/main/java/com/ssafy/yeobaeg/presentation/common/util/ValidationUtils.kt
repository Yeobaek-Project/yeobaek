package com.ssafy.yeobaeg.presentation.common.util

import android.util.Patterns

/**
 * 입력값 검증 유틸리티
 */
object ValidationUtils {
    
    /**
     * 이메일 형식 검증
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * 비밀번호 검증
     * - 8자 이상
     * - 영문 포함
     * - 숫자 포함
     * - 특수문자 포함
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        
        return hasLetter && hasDigit && hasSpecialChar
    }
    
    /**
     * 비밀번호 확인 검증
     */
    fun isPasswordMatching(password: String, passwordConfirm: String): Boolean {
        return password.isNotBlank() && password == passwordConfirm
    }
    
    /**
     * 날짜 형식 검증 (YYYY-MM-DD)
     */
    fun isValidBirthDateFormat(birthDate: String): Boolean {
        if (birthDate.isBlank()) return false
        
        val dateRegex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        if (!dateRegex.matches(birthDate)) return false
        
        // 날짜 유효성 추가 검증
        return try {
            val parts = birthDate.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()
            
            if (year < 1900 || year > 2100) return false
            if (month < 1 || month > 12) return false
            if (day < 1 || day > 31) return false
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 닉네임 검증
     */
    fun isValidNickname(nickname: String): Boolean {
        return nickname.isNotBlank() && nickname.length <= 20
    }
    
    /**
     * 이메일 에러 메시지 가져오기
     */
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "이메일을 입력해주세요"
            !isValidEmail(email) -> "올바른 이메일 형식이 아닙니다"
            else -> null
        }
    }
    
    /**
     * 비밀번호 에러 메시지 가져오기
     */
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "비밀번호를 입력해주세요"
            password.length < 8 -> "비밀번호는 8자 이상이어야 합니다"
            !password.any { it.isLetter() } -> "비밀번호에 영문이 포함되어야 합니다"
            !password.any { it.isDigit() } -> "비밀번호에 숫자가 포함되어야 합니다"
            !password.any { !it.isLetterOrDigit() } -> "비밀번호에 특수문자가 포함되어야 합니다"
            else -> null
        }
    }
    
    /**
     * 비밀번호 확인 에러 메시지 가져오기
     */
    fun getPasswordConfirmErrorMessage(password: String, passwordConfirm: String): String? {
        return when {
            passwordConfirm.isBlank() -> "비밀번호를 다시 입력해주세요"
            password != passwordConfirm -> "비밀번호가 일치하지 않습니다"
            else -> null
        }
    }
    
    /**
     * 생년월일 에러 메시지 가져오기
     */
    fun getBirthDateErrorMessage(birthDate: String): String? {
        return when {
            birthDate.isBlank() -> "생년월일을 선택해주세요"
            !isValidBirthDateFormat(birthDate) -> "올바른 날짜 형식이 아닙니다"
            else -> null
        }
    }
    
    /**
     * 닉네임 에러 메시지 가져오기
     */
    fun getNicknameErrorMessage(nickname: String): String? {
        return when {
            nickname.isBlank() -> "닉네임을 입력해주세요"
            nickname.length > 20 -> "닉네임은 20자 이하여야 합니다"
            else -> null
        }
    }
}

