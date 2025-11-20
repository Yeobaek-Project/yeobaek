package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomMessages
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomMessagesResponse(
    val chatRoom: ChatRoomSummaryResponse,
    val messages: List<ChatMessageResponse>,
    val pagination: PaginationResponse
)

fun ChatRoomMessagesResponse.toDomain(): ChatRoomMessages {
    return ChatRoomMessages(
        chatRoom = chatRoom.toDomain(),
        messages = messages.map { it.toDomain() },
        pagination = pagination.toDomain()

    )
}