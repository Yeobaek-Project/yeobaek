package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.cafe.CafeListResult
import kotlinx.serialization.Serializable

@Serializable
data class CafeListResponse(
    val cafes: List<CafeSummaryResponse>,
    val pagination: PaginationResponse
)

fun CafeListResponse.toDomain(): CafeListResult {
    return CafeListResult(
        cafes = cafes.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}

