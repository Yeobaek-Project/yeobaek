package com.ssafy.yeobaeg.domain.model.profile

import com.ssafy.yeobaeg.domain.model.Pagination

data class BookmarkCafe(
    val bookmarkCafes: List<BookmarkCafeSummary>,
    val pagination: Pagination
)