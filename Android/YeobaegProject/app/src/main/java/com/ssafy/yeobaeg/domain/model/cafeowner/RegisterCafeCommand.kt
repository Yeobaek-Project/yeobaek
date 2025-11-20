// domain model
package com.ssafy.yeobaeg.domain.model.cafeowner

import com.ssafy.yeobaeg.presentation.home.cafeowner.register.VisitorsBand

data class RegisterCafe(
    val cafeName: String,
    val address: String,
    val description: String,
    val contact: String,
    val businessHoursStart: String,
    val businessHoursEnd: String,
    val categoryIds: List<Long>,
    val dailyVisitors: VisitorsBand,
    val images: List<String>
)
