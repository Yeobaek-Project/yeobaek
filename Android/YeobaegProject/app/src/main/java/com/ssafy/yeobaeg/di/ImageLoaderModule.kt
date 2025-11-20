package com.ssafy.yeobaeg.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // 메모리의 25% 사용
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(512 * 1024 * 1024) // 512MB
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED) // 메모리 캐시 강제 활성화
            .diskCachePolicy(CachePolicy.ENABLED) // 디스크 캐시 강제 활성화
            .networkCachePolicy(CachePolicy.ENABLED) // 네트워크 캐시 강제 활성화
            .respectCacheHeaders(false) // 서버의 캐시 헤더 무시하고 무조건 캐싱
            .logger(DebugLogger()) // 디버그 로그 활성화 (개발 중에만 사용)
            .build()
    }
}
