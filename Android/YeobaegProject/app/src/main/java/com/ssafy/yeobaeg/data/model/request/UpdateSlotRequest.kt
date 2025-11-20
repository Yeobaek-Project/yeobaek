package com.ssafy.yeobaeg.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateSlotRequest(
    val commissionRate: Int? = null,
    val requirements: String? = null
)