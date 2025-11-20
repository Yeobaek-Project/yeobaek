package com.ssafy.yeobaeg.domain.repository

interface AnalyticsRepository {

    suspend fun logCafeViewEnd(
        cafeId: Long,
        dwell: Long,
        scrollPct: Int
    ): Result<Unit>

    suspend fun logArtworkViewEnd(
        artworkId: Long,
        dwell: Long,
        scrollPct: Int
    ): Result<Unit>

    suspend fun logCafeInquire(
        cafeId: Long
    ): Result<Unit>

    suspend fun logArtworkInquire(
        artworkId: Long
    ): Result<Unit>

    suspend fun logCafeDetailClick(
        cafeId: Long
    ): Result<Unit>

    suspend fun logArtworkDetailClick(
        artworkId: Long
    ): Result<Unit>
}