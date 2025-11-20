package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.auth.EmailCheckRequest
import com.ssafy.yeobaeg.data.model.auth.EmailCheckResponse
import com.ssafy.yeobaeg.data.model.auth.LoginRequest
import com.ssafy.yeobaeg.data.model.auth.LoginResponse
import com.ssafy.yeobaeg.data.model.auth.SignUpRequest
import com.ssafy.yeobaeg.data.model.auth.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<BaseResponse<Unit?>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<LoginResponse>>

    @POST("auth/reissue")
    suspend fun refresh(@Header("Authorization") refreshToken: String): Response<BaseResponse<TokenResponse>>

    @POST("auth/email/check")
    suspend fun checkEmail(@Body request: EmailCheckRequest): Response<BaseResponse<EmailCheckResponse>>
}

