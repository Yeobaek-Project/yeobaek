package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.domain.model.cafe.CafeImage
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class CafeImageDto(val imageUrl: String)

fun CafeImageDto.toDomain(): CafeImage {
    return CafeImage(
        imageUrl = imageUrl
    )
}
