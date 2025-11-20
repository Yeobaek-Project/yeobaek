package com.ssafy.yeobaeg.domain.model.chat

import com.ssafy.yeobaeg.domain.model.Pagination

data class ChatRoomMessages(
    val chatRoom: ChatRoomSummary= ChatRoomSummary(),
    val messages: List<ChatMessage> = emptyList(),
    val pagination: Pagination= Pagination()
)