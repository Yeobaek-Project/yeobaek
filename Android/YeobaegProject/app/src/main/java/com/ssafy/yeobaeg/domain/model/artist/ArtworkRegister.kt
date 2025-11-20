package com.ssafy.yeobaeg.domain.model.artist

import com.ssafy.yeobaeg.domain.model.ArtworkCategory


data class ArtworkRegister(
    val title: String="",
    val description: String="",
    val price: Int=0,
    val category: ArtworkCategory = ArtworkCategory.LANDSCAPE,
    val widthCm: Int=0,
    val heightCm: Int=0,
    val weightKg: Double=0.0,
    val productionYear: String="",
    val images: List<String> = emptyList<String>()
)