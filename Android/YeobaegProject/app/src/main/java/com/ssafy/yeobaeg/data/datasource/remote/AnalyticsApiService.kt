package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST

interface AnalyticsApiService {

    @POST("analytics/events")
    suspend fun logEvent(
        @Body request: LogEventRequest
    ): BaseResponse<Unit>
}

@Serializable
data class LogEventRequest(
    val eventType: String,
    val targetType: String,
    val targetId: Long,
    val durationSeconds: Int? = null,
    val maxScrollPercent: Int? = null
)