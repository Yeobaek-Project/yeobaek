package com.ssafy.yeobaeg.data.network

import com.ssafy.yeobaeg.data.datasource.local.AuthDataSource
import com.ssafy.yeobaeg.data.datasource.remote.AuthApiService
import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.auth.TokenResponse
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 401 Unauthorized 응답을 받았을 때 자동으로 토큰을 갱신하는 Authenticator
 */
@Singleton
class AuthAuthenticator @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val authApiService: AuthApiService,
    private val authSessionManager: AuthSessionManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // refresh token으로 새 토큰 발급
        return runBlocking {
            try {
                val refreshToken = authDataSource.getRefreshToken()
                if (refreshToken == null) {
                    // refresh token이 없으면 로그아웃 처리
                    authDataSource.clearTokens()
                    return@runBlocking null
                }

                // refresh API 호출
                val refreshResponse = authApiService.refresh(
                    refreshToken = "Bearer $refreshToken"
                )

                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body()
                    val tokenResponse = body?.data
                    
                    if (tokenResponse != null) {
                        // 새 토큰 저장
                        authDataSource.saveTokens(
                            accessToken = tokenResponse.accessToken,
                            refreshToken = tokenResponse.refreshToken
                        )

                        // 원래 요청을 새 토큰으로 재시도
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                            .build()
                    } else {
                        // 응답이 비어있으면 로그아웃 + 로그인 화면 이동 이벤트 발행
                        authDataSource.clearTokens()
                        authSessionManager.emitTokenExpired()
                        null
                    }
                } else {
                    // refresh 실패 시 로그아웃 처리 + 로그인 화면 이동 이벤트 발행
                    authDataSource.clearTokens()
                    authSessionManager.emitTokenExpired()
                    null
                }
            } catch (e: Exception) {
                // 예외 발생 시 로그아웃 처리 + 로그인 화면 이동 이벤트 발행
                authDataSource.clearTokens()
                authSessionManager.emitTokenExpired()
                null
            }
        }
    }
}

