package com.ssafy.yeobaeg.domain.model.chat

import com.ssafy.yeobaeg.domain.model.Pagination

data class ChatList(
    val chatRooms: List<ChatRoomInfo>,
    val pagination: Pagination
)