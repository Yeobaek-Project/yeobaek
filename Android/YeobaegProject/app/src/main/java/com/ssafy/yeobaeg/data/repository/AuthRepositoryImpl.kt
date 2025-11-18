package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.local.AuthDataSource
import com.ssafy.yeobaeg.data.datasource.remote.AuthApiService
import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.auth.EmailCheckRequest
import com.ssafy.yeobaeg.data.model.auth.LoginRequest
import com.ssafy.yeobaeg.data.model.auth.SignUpRequest
import com.ssafy.yeobaeg.di.StoreJson
import com.ssafy.yeobaeg.domain.model.LoginResult
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val authDataSource: AuthDataSource,
    @StoreJson private val json: Json
) : AuthRepository {
    
    override suspend fun signUp(
        userType: String,
        email: String,
        password: String,
        passwordConfirm: String,
        nickname: String,
        birthDate: String,
        gender: String
    ): Result<String> {
        return try {
            val request = SignUpRequest(
                userType = userType,
                email = email,
                password = password,
                passwordConfirm = passwordConfirm,
                nickname = nickname,
                birthDate = birthDate,
                gender = gender
            )
            
            val response = authApiService.signUp(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.message ?: "정상 처리")
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = json.decodeFromString<BaseResponse<Unit?>>(errorBody)
                        errorResponse.message ?: "회원가입에 실패했습니다"
                    } catch (e: Exception) {
                        errorBody
                    }
                } else {
                    "회원가입에 실패했습니다"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val request = LoginRequest(
                email = email,
                password = password
            )
            
            val response = authApiService.login(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                val loginResponse = body?.data
                
                if (loginResponse != null) {
                    // 토큰 저장
                    authDataSource.saveTokens(
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken
                    )

                    // userId 저장
                    authDataSource.saveUserId(loginResponse.userId)

                    // UserRole 변환
                    val role = when (loginResponse.userType) {
                        "GENERAL" -> UserRole.GENERAL
                        "CAFE_OWNER" -> UserRole.CAFE_OWNER
                        "ARTIST" -> UserRole.ARTIST
                        else -> UserRole.GENERAL
                    }

                    // LoginResult 반환 (UserRole과 userId 포함)
                    val loginResult = LoginResult(
                        userRole = role,
                        userId = loginResponse.userId
                    )
                    Result.success(loginResult)
                } else {
                    Result.failure(Exception("로그인 응답 데이터가 없습니다"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = json.decodeFromString<BaseResponse<Unit?>>(errorBody)
                        errorResponse.message ?: "로그인에 실패했습니다"
                    } catch (e: Exception) {
                        errorBody
                    }
                } else {
                    "로그인에 실패했습니다"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            // 토큰 삭제
            authDataSource.clearTokens()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkEmailAvailability(email: String): Result<Boolean> {
        return try {
            val request = EmailCheckRequest(email = email)
            val response = authApiService.checkEmail(request)

            if (response.isSuccessful) {
                val body = response.body()
                val emailCheckResponse = body?.data

                if (emailCheckResponse != null) {
                    Result.success(emailCheckResponse.available)
                } else {
                    Result.failure(Exception("이메일 중복 확인 응답 데이터가 없습니다"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = json.decodeFromString<BaseResponse<Unit?>>(errorBody)
                        errorResponse.message ?: "이메일 중복 확인에 실패했습니다"
                    } catch (e: Exception) {
                        errorBody
                    }
                } else {
                    "이메일 중복 확인에 실패했습니다"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

