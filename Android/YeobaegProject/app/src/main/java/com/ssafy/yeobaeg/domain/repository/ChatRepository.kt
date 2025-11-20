package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.chat.ChatList
import com.ssafy.yeobaeg.domain.model.chat.ChatRoom
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomMessages
import com.ssafy.yeobaeg.domain.model.chat.WriteReview

interface ChatRepository {

    suspend fun getChatList(
        sortBy: String = "LATEST",
        page: Int = 0,
        size: Int = 20
    ): Result<ChatList>

    suspend fun createChatRoom(partnerId: Long): Result<ChatRoom>

    suspend fun getChatRoomMessages(
        chatRoomId: Long, page: Int = 0,
        size: Int = 20
    ): Result<ChatRoomMessages>

    suspend fun writeReview(
        request: WriteReview
    ):Result<Unit>
}
