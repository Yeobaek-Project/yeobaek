package com.ssafy.yeobaeg.data.model.exhibition.response

import com.ssafy.yeobaeg.domain.model.exhibition.CafeSummaryInfo
import kotlinx.serialization.Serializable

@Serializable
data class CafeSummaryResponse(
    val cafeId: Long,
    val cafeName: String,
    val address: String
)

fun CafeSummaryResponse.toDomain(): CafeSummaryInfo {
    return CafeSummaryInfo(
        cafeId = cafeId,
        cafeName = cafeName,
        address = address
    )
}