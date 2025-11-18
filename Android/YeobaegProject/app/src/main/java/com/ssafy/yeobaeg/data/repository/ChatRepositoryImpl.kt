package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.ChatApiService
import com.ssafy.yeobaeg.data.model.chat.request.CreateChatRoomRequest
import com.ssafy.yeobaeg.data.model.chat.request.WriteReviewRequest
import com.ssafy.yeobaeg.data.model.chat.response.toDomain
import com.ssafy.yeobaeg.domain.model.chat.ChatList
import com.ssafy.yeobaeg.domain.model.chat.ChatRoom
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomMessages
import com.ssafy.yeobaeg.domain.model.chat.WriteReview
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService,
) : ChatRepository {
    override suspend fun getChatList(
        sortBy: String,
        page: Int,
        size: Int
    ): Result<ChatList> {
        return runCatching {
            val response = chatApiService.getChatList(
                sortBy = sortBy,
                page = page,
                size = size
            )
            response.data?.toDomain()
                ?: throw IllegalStateException("Chat list response data is null")
        }
    }

    override suspend fun createChatRoom(partnerId: Long): Result<ChatRoom> {
        return runCatching {
            val request = CreateChatRoomRequest(partnerId = partnerId)
            val response = chatApiService.createChatRoom(request)

            val chatRoom = response.data?.chatRoom
                ?: throw IllegalStateException("Chat room creation response data.chatRoom is null")

            chatRoom.toDomain()
        }
    }

    override suspend fun getChatRoomMessages(
        chatRoomId: Long,
        page: Int,
        size: Int
    ): Result<ChatRoomMessages> {
        return runCatching {
            val response = chatApiService.getChattingMessages(
                chatRoomId = chatRoomId,
                page = page,
                size = size
            )
            response.data?.toDomain()
                ?: throw IllegalStateException("Chat Room Messages response data is null")
        }
    }

    override suspend fun writeReview(request: WriteReview): Result<Unit> {
        return runCatching {
            val body = WriteReviewRequest(
                exhibitionId = request.exhibitionId,
                revieweeId = request.reviewId,
                rating = request.rating,
                comment = request.comment
            )

            chatApiService.writeReview(body)
        }
    }
}