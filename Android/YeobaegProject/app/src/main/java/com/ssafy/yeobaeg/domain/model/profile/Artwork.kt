package com.ssafy.yeobaeg.domain.model.profile

import com.ssafy.yeobaeg.domain.model.Pagination

data class Artwork(
    val artworks: List<ArtworkSummary>,
    val pagination: Pagination = Pagination()
)