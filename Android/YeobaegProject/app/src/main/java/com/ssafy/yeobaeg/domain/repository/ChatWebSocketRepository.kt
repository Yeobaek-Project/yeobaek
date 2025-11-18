package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.data.model.ws.resquest.SendMessageRequest
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.domain.model.ws.SendMessage
import kotlinx.coroutines.flow.Flow

interface ChatWebSocketRepository {
    suspend fun connect()
    fun subscribeToRoom(chatRoomId: Long)
    fun sendMessage(request: SendMessage)
    fun disconnectWebSocket()
    fun unsubscribeFromRoom(chatRoomId:Long)
    fun receiveChatMessages(): Flow<ChatMessage>
    fun receiveGlobalEvents(): Flow<String>
}