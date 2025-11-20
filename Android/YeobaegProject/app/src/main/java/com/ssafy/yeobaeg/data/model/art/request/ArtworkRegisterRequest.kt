package com.ssafy.yeobaeg.data.model.art.request

import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.artist.ArtworkRegister
import kotlinx.serialization.Serializable

@Serializable
data class ArtworkRegisterRequest(
    val title: String,
    val description: String,
    val price: Int,
    val category: ArtworkCategory,
    val widthCm: Int,
    val heightCm: Int,
    val weightKg: Double,
    val productionYear: String,
    val images: List<String>
)
fun ArtworkRegister.toDto(): ArtworkRegisterRequest =
    ArtworkRegisterRequest(
        title = title,
        description=description,
        price=price,
        category = category,
        widthCm=widthCm,
        heightCm=heightCm,
        weightKg=weightKg,
        productionYear=productionYear,
        images=images
    )