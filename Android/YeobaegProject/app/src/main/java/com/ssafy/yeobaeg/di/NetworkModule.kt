package com.ssafy.yeobaeg.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ssafy.yeobaeg.BuildConfig
import com.ssafy.yeobaeg.data.datasource.local.TokenDataSource
import com.ssafy.yeobaeg.data.datasource.remote.AnalyticsApiService
import com.ssafy.yeobaeg.data.datasource.remote.ArtworkApiService
import com.ssafy.yeobaeg.data.datasource.remote.AuthApiService
import com.ssafy.yeobaeg.data.datasource.remote.CafeApiService
import com.ssafy.yeobaeg.data.datasource.remote.ChatApiService
import com.ssafy.yeobaeg.data.datasource.remote.ChatWebSocketService
import com.ssafy.yeobaeg.data.datasource.remote.ExhibitionApiService
import com.ssafy.yeobaeg.data.datasource.remote.ProfileApiService
import com.ssafy.yeobaeg.data.datasource.remote.StorageApiService
import com.ssafy.yeobaeg.data.network.AuthAuthenticator
import com.ssafy.yeobaeg.data.network.AuthTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeneralOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeneralRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 공통: Json 설정
    @Provides
    @Singleton
    @NetworkJson
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        isLenient = true
        encodeDefaults = true
    }

    // 공통: 로깅 인터셉터
    private fun loggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

    // 인증 전용 OkHttp (인터셉터/인증자 없음) - 순환참조 방지용
    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    // 일반 OkHttp (토큰 인터셉터 + 인증자 포함)
    @Provides
    @Singleton
    @GeneralOkHttpClient
    fun provideGeneralOkHttpClient(
        authTokenInterceptor: AuthTokenInterceptor,
        authAuthenticator: AuthAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .addInterceptor(authTokenInterceptor)
            .authenticator(authAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    // 일반 Retrofit
    @Provides
    @Singleton
    @GeneralRetrofit
    fun provideGeneralRetrofit(
        @GeneralOkHttpClient okHttpClient: OkHttpClient,
        @NetworkJson json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // 인증 전용 Retrofit
    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        @NetworkJson json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    // === 서비스 바인딩 ===
    // 토큰 재발급 등 인증 전용 API → AuthRetrofit
    @Provides
    @Singleton
    fun provideAuthApiService(@AuthRetrofit retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    // 일반 API → GeneralRetrofit
    @Provides
    @Singleton
    fun provideProfileApiService(@GeneralRetrofit retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun provideCafeApiService(@GeneralRetrofit retrofit: Retrofit): CafeApiService =
        retrofit.create(CafeApiService::class.java)

    @Provides
    @Singleton
    fun provideArtApiService(@GeneralRetrofit retrofit: Retrofit): ArtworkApiService =
        retrofit.create(ArtworkApiService::class.java)

    @Provides
    @Singleton
    fun provideStorageApiService(@GeneralRetrofit retrofit: Retrofit): StorageApiService =
        retrofit.create(StorageApiService::class.java)

    @Provides
    @Singleton
    fun provideChatApiService(@GeneralRetrofit retrofit: Retrofit): ChatApiService =
        retrofit.create(ChatApiService::class.java)

    @Provides
    @Singleton
    fun provideChatWebSocketService(
        tokenDataSource: TokenDataSource
    ): ChatWebSocketService = ChatWebSocketService(tokenDataSource)

    @Provides
    @Singleton
    fun provideExhibitionApiService(@GeneralRetrofit retrofit: Retrofit): ExhibitionApiService =
        retrofit.create(ExhibitionApiService::class.java)

    @Provides
    @Singleton
    fun provideAnalyticsApiService(@GeneralRetrofit retrofit: Retrofit): AnalyticsApiService =
        retrofit.create(AnalyticsApiService::class.java)
}
