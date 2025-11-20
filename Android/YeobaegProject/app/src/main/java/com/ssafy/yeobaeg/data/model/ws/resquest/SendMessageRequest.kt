package com.ssafy.yeobaeg.data.model.ws.resquest

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val chatRoomId: Long,
    val messageType: String,
    val content: String? = null,
    val imageUrl: String? = null
)

