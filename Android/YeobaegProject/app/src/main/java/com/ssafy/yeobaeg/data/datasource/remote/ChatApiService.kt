package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.chat.request.CreateChatRoomRequest
import com.ssafy.yeobaeg.data.model.chat.request.WriteReviewRequest
import com.ssafy.yeobaeg.data.model.chat.response.ChatListResponse
import com.ssafy.yeobaeg.data.model.chat.response.ChatRoomMessagesResponse
import com.ssafy.yeobaeg.data.model.chat.response.ChatRoomResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @GET("chats")
    suspend fun getChatList(
        @Query("sortBy") sortBy: String = "LATEST",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): BaseResponse<ChatListResponse>

    @POST("chats")
    suspend fun createChatRoom(
        @Body request: CreateChatRoomRequest
    ): BaseResponse<ChatRoomResponse>

    @GET("chats/{chatRoomId}/messages")
    suspend fun getChattingMessages(
        @Path("chatRoomId") chatRoomId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): BaseResponse<ChatRoomMessagesResponse>

    @POST("reviews")
    suspend fun writeReview(
        @Body request: WriteReviewRequest
    ): BaseResponse<Unit>
}