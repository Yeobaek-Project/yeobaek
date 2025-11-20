package com.ssafy.yeobaeg.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsLogger(
    private val fa: FirebaseAnalytics
) : AnalyticsLogger {

    companion object {
        // event names
        private const val EVENT_CAFE_CLICK = "cafe_click"
        private const val EVENT_CAFE_VIEW_END = "cafe_view_end"
        private const val EVENT_CAFE_LIKE = "cafe_like"
        private const val EVENT_CAFE_UNLIKE = "cafe_unlike"

        private const val EVENT_ARTWORK_CLICK = "artwork_click"
        private const val EVENT_ARTWORK_VIEW_END = "artwork_view_end"
        private const val EVENT_ARTWORK_LIKE = "artwork_like"
        private const val EVENT_ARTWORK_UNLIKE = "artwork_unlike"

        private const val EVENT_ARTIST_CLICK = "artist_click"

        private const val EVENT_SEARCH_ART = "search_art"
        private const val EVENT_SEARCH_CAFE = "search_cafe"

        // common param keys (use simple names; register in console if you want to use them in UI)
        private const val PARAM_CAFE_ID = "cafe_id"
        private const val PARAM_ARTWORK_ID = "artwork_id"
        private const val PARAM_ARTIST_ID = "artist_id"
        private const val PARAM_DWELL_MS = "dwell_ms"
        private const val PARAM_SCROLL_PCT = "scroll_pct"
        private const val PARAM_QUERY = "query"
    }

    /**
     * 현재 사용자 ID를 설정
     * - 로그인 시점에 호출
     */
    override fun setUserId(userId: Long) {
        fa.setUserId(userId.toString())
    }

    override fun logCafeClick(cafeId: Long) {
        fa.logEvent(EVENT_CAFE_CLICK) {
            putLong(PARAM_CAFE_ID, cafeId)
        }
    }

    override fun logCafeViewEnd(cafeId: Long, dwellMs: Long, scrollPct: Int) {
        fa.logEvent(EVENT_CAFE_VIEW_END) {
            putLong(PARAM_CAFE_ID, cafeId)
            putLong(PARAM_DWELL_MS, dwellMs)
            putInt(PARAM_SCROLL_PCT, scrollPct)
        }
    }

    override fun logCafeLike(cafeId: Long) {
        fa.logEvent(EVENT_CAFE_LIKE) {
            putLong(PARAM_CAFE_ID, cafeId)
        }
    }

    override fun logCafeUnlike(cafeId: Long) {
        fa.logEvent(EVENT_CAFE_UNLIKE) {
            putLong(PARAM_CAFE_ID, cafeId)
        }
    }

    override fun logArtworkClick(artworkId: Long) {
        fa.logEvent(EVENT_ARTWORK_CLICK) {
            putLong(PARAM_ARTWORK_ID, artworkId)
        }
    }

    override fun logArtworkViewEnd(
        artworkId: Long,
        dwellMs: Long,
        scrollPct: Int
    ) {
        fa.logEvent(EVENT_ARTWORK_VIEW_END) {
            putLong(PARAM_ARTWORK_ID, artworkId)
            putLong(PARAM_DWELL_MS, dwellMs)
            putInt(PARAM_SCROLL_PCT, scrollPct)
        }
    }

    override fun logArtworkLike(artworkId: Long) {
        fa.logEvent(EVENT_ARTWORK_LIKE) {
            putLong(PARAM_ARTWORK_ID, artworkId)
        }
    }

    override fun logArtworkUnlike(artworkId: Long) {
        fa.logEvent(EVENT_ARTWORK_UNLIKE) {
            putLong(PARAM_ARTWORK_ID, artworkId)
        }
    }

    override fun logArtistClick(artistId: Long) {
        fa.logEvent(EVENT_ARTIST_CLICK) {
            putLong(PARAM_ARTIST_ID, artistId)
        }
    }

    override fun logSearchArt(query: String) {
        fa.logEvent(EVENT_SEARCH_ART) {
            putString(PARAM_QUERY, query)
        }
    }

    override fun logSearchCafe(query: String) {
        fa.logEvent(EVENT_SEARCH_CAFE) {
            putString(PARAM_QUERY, query)
        }
    }

    private inline fun FirebaseAnalytics.logEvent(
        name: String,
        crossinline block: Bundle.() -> Unit
    ) {
        val bundle = Bundle().apply(block)
        logEvent(name, bundle)
    }
}
