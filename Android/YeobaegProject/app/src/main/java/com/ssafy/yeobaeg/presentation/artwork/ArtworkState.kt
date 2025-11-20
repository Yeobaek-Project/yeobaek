package com.ssafy.yeobaeg.presentation.artwork

import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.Pagination
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary


enum class SearchMode(val title: String) {
    BROWSE("작품 둘러보기"),
    RECENT("검색"),
    RESULT("검색 결과")
}

data class ArtExploreState(
    val mode: SearchMode = SearchMode.BROWSE,
    val query: String = "",
    val recent: List<String> = emptyList(),
    val selectedCategory: ArtworkCategory = ArtworkCategory.ALL,
    val categories: List<ArtworkCategory> = ArtworkCategory.entries,
    val artworks: List<ArtworkSummary> = emptyList(),
    val resultArtists: List<ArtistSummary> = emptyList(),
    val resultArtworks: List<ArtworkSummary> = emptyList(),
    val paginationInfo: Pagination? = null,
    val maxWidthCm: Int? = null,
    val maxHeightCm: Int? = null,
    val minWidthCm: Int? = null,
    val minHeightCm: Int? = null,
    val isSearchLoading: Boolean = false
)