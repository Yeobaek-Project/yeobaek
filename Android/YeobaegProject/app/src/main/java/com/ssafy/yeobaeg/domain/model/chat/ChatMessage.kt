package com.ssafy.yeobaeg.domain.model.chat

import com.ssafy.yeobaeg.data.model.chat.response.ExhibitionInfoResponse
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val messageId: Long,
    val senderId: Long? = null,
    val messageType: MessageType,
    val content: String? = null,
    val imageUrl: String? = null,
    val exhibitionId: Long? = null,
    val exhibition: ExhibitionInfoResponse? = null,
    val isRead: Boolean,
    val createdAt: String
)