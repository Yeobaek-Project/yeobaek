package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.LoginResult
import com.ssafy.yeobaeg.domain.model.UserRole

interface AuthRepository {
    suspend fun signUp(
        userType: String,
        email: String,
        password: String,
        passwordConfirm: String,
        nickname: String,
        birthDate: String,
        gender: String
    ): Result<String>

    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun logout(): Result<Unit>
    suspend fun checkEmailAvailability(email: String): Result<Boolean>
}

