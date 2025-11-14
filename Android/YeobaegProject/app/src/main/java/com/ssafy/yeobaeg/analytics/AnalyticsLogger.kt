// AnalyticsLogger.kt
package com.ssafy.yeobaeg.analytics


interface AnalyticsLogger {
    fun setUserId(userId: Long)

    fun logCafeClick(cafeId: Long)

    fun logCafeViewEnd(cafeId: Long, dwellMs: Long, scrollPct: Int)

    fun logCafeLike(cafeId: Long)

    fun logCafeUnlike(cafeId: Long)

    fun logArtworkClick(artworkId: Long)

    fun logArtworkViewEnd(artworkId: Long, dwellMs: Long, scrollPct: Int)

    fun logArtworkLike(artworkId: Long)

    fun logArtworkUnlike(artworkId: Long)

    fun logArtistClick(artistId: Long)

    fun logSearchArt(query: String)

    fun logSearchCafe(query: String)
}