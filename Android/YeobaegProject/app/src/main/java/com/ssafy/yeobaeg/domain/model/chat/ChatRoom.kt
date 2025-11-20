package com.ssafy.yeobaeg.domain.model.chat

data class ChatRoom(
    val chatRoomId: Long,
    val partnerId: Long,
    val partnerName: String,
    val partnerImageUrl: String,
    val partnerRole: String
)