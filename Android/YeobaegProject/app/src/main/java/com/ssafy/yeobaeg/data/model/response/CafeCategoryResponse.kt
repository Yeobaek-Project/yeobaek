package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.cafe.CafeCategory
import kotlinx.serialization.Serializable

@Serializable
data class CafeCategoryResponse(
    val categories: List<CategoryResponse>
)

@Serializable
data class CategoryResponse(
    val categoryId: Long,
    val categoryName: String
)

fun CategoryResponse.toDomain() = CafeCategory(
    categoryId = categoryId,
    categoryName = categoryName
)