package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.AnalyticsApiService
import com.ssafy.yeobaeg.data.datasource.remote.LogEventRequest
import com.ssafy.yeobaeg.di.StoreJson
import com.ssafy.yeobaeg.domain.repository.AnalyticsRepository
import handleApi
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsApiService: AnalyticsApiService,
    @StoreJson private val json: Json
) : AnalyticsRepository {

    override suspend fun logCafeViewEnd(
        cafeId: Long,
        dwell: Long,
        scrollPct: Int
    ) = runCatching {
        handleApi(json) {
            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "DWELL",
                    targetType = "CAFE",
                    targetId = cafeId,
                    durationSeconds = (dwell / 1000).toInt(),
                )
            )

            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "SCROLL",
                    targetType = "CAFE",
                    targetId = cafeId,
                    maxScrollPercent = scrollPct
                )
            )
        }
    }

    override suspend fun logArtworkViewEnd(
        artworkId: Long,
        dwell: Long,
        scrollPct: Int
    ) = runCatching {
        handleApi(json) {
            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "DWELL",
                    targetType = "ARTWORK",
                    targetId = artworkId,
                    durationSeconds = (dwell / 1000).toInt(),
                )
            )

            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "SCROLL",
                    targetType = "ARTWORK",
                    targetId = artworkId,
                    maxScrollPercent = scrollPct
                )
            )
        }
    }

    override suspend fun logCafeInquire(cafeId: Long) = runCatching {
        handleApi(json) {
            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "CHAT",
                    targetType = "CAFE",
                    targetId = cafeId,
                )
            )
        }
    }


    override suspend fun logArtworkInquire(artworkId: Long) = runCatching {
        handleApi(json) {
            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "CHAT",
                    targetType = "ARTWORK",
                    targetId = artworkId,
                )
            )
        }
    }

    override suspend fun logCafeDetailClick(cafeId: Long) = runCatching {
        handleApi(json) {
            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "VIEW",
                    targetType = "CAFE",
                    targetId = cafeId,
                )
            )
        }
    }

    override suspend fun logArtworkDetailClick(artworkId: Long) = runCatching {
        handleApi(json) {
            analyticsApiService.logEvent(
                LogEventRequest(
                    eventType = "VIEW",
                    targetType = "ARTWORK",
                    targetId = artworkId,
                )
            )
        }
    }
}