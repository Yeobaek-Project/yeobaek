package com.ssafy.yeobaeg.domain.model.ws

data class SendMessage(
    val chatRoomId: Long,
    val messageType: String,
    val content: String? = null,
    val imageUrl: String? = null
)

