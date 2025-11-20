package com.ssafy.yeobaeg.data.model.request

import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe
import kotlinx.serialization.Serializable

@Serializable
data class RegisterCafeRequest(
    val address: String,
    val businessHoursEnd: String,
    val businessHoursStart: String,
    val cafeName: String,
    val categoryIds: List<Int>,
    val contact: String,
    val dailyVisitors: String,
    val description: String,
    val images: List<String>
)

fun RegisterCafe.toDto(): RegisterCafeRequest =
    RegisterCafeRequest(
        address = address,
        businessHoursStart = businessHoursStart,
        businessHoursEnd = businessHoursEnd,
        cafeName = cafeName,
        categoryIds = categoryIds.map { it.toInt() },
        contact = contact,
        dailyVisitors = dailyVisitors.label,
        description = description,
        images = images
    )