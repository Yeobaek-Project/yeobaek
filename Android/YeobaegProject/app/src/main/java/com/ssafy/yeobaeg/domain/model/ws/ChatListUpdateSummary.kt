package com.ssafy.yeobaeg.domain.model.ws

import kotlinx.serialization.Serializable

@Serializable
data class ChatListUpdateSummary(
    val chatRoomId: Long,
    val unreadCount: Long,
    val lastMessage: String? = null,
    val lastMessageType: String,
    val lastMessageAt: String
)
