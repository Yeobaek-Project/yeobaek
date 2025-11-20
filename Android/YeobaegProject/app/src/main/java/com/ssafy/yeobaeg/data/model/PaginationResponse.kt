package com.ssafy.yeobaeg.data.model

import com.ssafy.yeobaeg.domain.model.Pagination
import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse(
    val currentPage: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean
)

fun PaginationResponse.toDomain(): Pagination {
    return Pagination(
        currentPage = currentPage,
        hasNext = hasNext,
        totalPages = totalPages,
        totalElements = totalElements,
        pageSize = pageSize
    )
}