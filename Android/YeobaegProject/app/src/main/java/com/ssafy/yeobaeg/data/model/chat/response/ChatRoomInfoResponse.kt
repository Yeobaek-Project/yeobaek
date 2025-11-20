package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ChatRoomInfo
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomInfoResponse (
    val chatRoomId: Long,
    val partnerId: Long,
    val partnerName: String,
    val partnerImageUrl: String?=null,
    val partnerRole: String,
    val unreadCount: Int?,
    val lastMessage: String?,
    val lastMessageType: String?,
    val lastMessageAt: String?
)

fun ChatRoomInfoResponse.toDomain(): ChatRoomInfo {
    return ChatRoomInfo(
        chatRoomId = chatRoomId,
        partnerId = partnerId,
        partnerName = partnerName,
        partnerImageUrl = partnerImageUrl,
        partnerRole = partnerRole,
        unreadCount = unreadCount,
        lastMessage = lastMessage,
        lastMessageType = lastMessageType,
        lastMessageAt = lastMessageAt
    )
}