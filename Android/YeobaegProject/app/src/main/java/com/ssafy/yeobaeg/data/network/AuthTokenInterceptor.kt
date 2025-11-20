package com.ssafy.yeobaeg.data.network

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.local.AuthDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 모든 API 요청에 Authorization 헤더를 자동으로 추가하는 Interceptor
 */
@Singleton
class AuthTokenInterceptor @Inject constructor(
    private val authDataSource: AuthDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // DataStore에서 토큰 가져오기 (코루틴 블로킹)
        val accessToken = runBlocking {
            authDataSource.getAccessToken()
        }

        // 토큰이 있으면 헤더에 추가
        val requestWithToken = if (accessToken != null) {
            Log.d("AuthTokenInterceptor", "Adding Authorization header with token: $accessToken")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(requestWithToken)
    }
}

