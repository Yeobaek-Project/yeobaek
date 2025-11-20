package com.ssafy.yeobaeg.data.model.chat.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRoomRequest (
    val partnerId:Long
)