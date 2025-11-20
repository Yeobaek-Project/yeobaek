package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomSummaryResponse(
    val chatRoomId: Long,
    val partnerId: Long,
    val partnerName: String,
    val partnerImageUrl: String?=null,
    val partnerRole: String,
)

fun ChatRoomSummaryResponse.toDomain(): ChatRoomSummary {
    return ChatRoomSummary(
        chatRoomId = chatRoomId,
        partnerId = partnerId,
        partnerName = partnerName,
        partnerImageUrl = partnerImageUrl,
        partnerRole = partnerRole,
    )
}