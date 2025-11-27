package com.ssafy.yeobaeg.data.model.ws.response

import com.ssafy.yeobaeg.domain.model.ws.ChatListUpdateSummary
import kotlinx.serialization.Serializable

@Serializable
data class ChatListUpdateSummaryResponse(
    val chatRoomId: Long,
    val unreadCount: Long,
    val lastMessage: String? = null,
    val lastMessageType: String,
    val lastMessageAt: String
)

fun ChatListUpdateSummaryResponse.toDomain(): ChatListUpdateSummary {
    return ChatListUpdateSummary(
        chatRoomId = chatRoomId,
        unreadCount = unreadCount,
        lastMessage = lastMessage,
        lastMessageType = lastMessageType,
        lastMessageAt = lastMessageAt
    )
}