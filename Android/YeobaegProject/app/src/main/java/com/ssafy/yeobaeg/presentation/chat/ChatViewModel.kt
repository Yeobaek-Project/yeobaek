package com.ssafy.yeobaeg.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomInfo
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomMessages
import com.ssafy.yeobaeg.domain.model.chat.MessageType
import com.ssafy.yeobaeg.domain.model.ws.SendMessage
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import com.ssafy.yeobaeg.domain.repository.ChatWebSocketRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ChatUiState(
    val chatRooms: List<ChatRoomInfo> = emptyList(),
    val isLoading: Boolean = false,
    val chatRoomMessages: ChatRoomMessages = ChatRoomMessages(),
    val errorMessage: String? = null,
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navigator: Navigator,
    private val chatRepository: ChatRepository,
    private val chatWebSocketRepository: ChatWebSocketRepository,
    private val authSessionManager: AuthSessionManager

) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState
    private var receiveJob: Job? = null
    val myUserId: StateFlow<Long?> = authSessionManager.currentUserId
    val myUserRole: StateFlow<UserRole?> = authSessionManager.currentUserRole

    /** 채팅방 구독 시작 (입장 시) */
    fun subscribeToChatRoom(chatRoomId: Long) {
        viewModelScope.launch {
            delay(500)
            chatWebSocketRepository.subscribeToRoom(chatRoomId)
            startReceivingMessages()
        }
    }


    /**채팅방 구독 해제 (퇴장 시) */
    fun unsubscribeFromChatRoom(chatRoomId: Long) {
        chatWebSocketRepository.unsubscribeFromRoom(chatRoomId)
        stopReceivingMessages()
    }

    /** 서버로부터 새 메시지 수신 */
    private fun startReceivingMessages() {
        if (receiveJob?.isActive == true) return
        receiveJob = viewModelScope.launch {
            chatWebSocketRepository.receiveMessages().collect { msgJson ->
                val newMessage = parseChatMessage(msgJson)
                if (newMessage.senderId != myUserId.value) {
                    _uiState.update { state ->
                        state.copy(
                            chatRoomMessages = state.chatRoomMessages.copy(
                                messages = state.chatRoomMessages.messages + newMessage
                            )
                        )
                    }
                }
            }
        }
    }

    private fun stopReceivingMessages() {
        receiveJob?.cancel()
        receiveJob = null
    }

    /** 메시지 JSON 파싱 */

    private fun parseChatMessage(json: String): ChatMessage {
        return try {
            Json.decodeFromString(ChatMessage.serializer(), json)
        } catch (e: Exception) {
            ChatMessage(0, 0L, MessageType.TEXT, "파싱 오류", "",1 , null,false, "")
        }
    }

    /** 메시지 전송 */
    fun sendMessage(text: String, myUserId: Long?) {
        val chatRoomId = _uiState.value.chatRoomMessages.chatRoom.chatRoomId
        val message = SendMessage(chatRoomId, "TEXT", text)
        chatWebSocketRepository.sendMessage(message)

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val nowString = formatter.format(Date())
        val myMsg = ChatMessage(
            messageId = System.currentTimeMillis(),
            senderId = myUserId,
            messageType = MessageType.TEXT,
            content = text,
            imageUrl = "",
            exhibition = null,
            isRead = true,
            createdAt = nowString
        )

        _uiState.update { state ->
            val updatedList = state.chatRoomMessages.messages + myMsg
            state.copy(chatRoomMessages = state.chatRoomMessages.copy(messages = updatedList))
        }
    }

    /** 채팅방 목록 불러오기 */
    fun loadChatRooms() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        chatRepository.getChatList()
            .onSuccess { chatList ->
                _uiState.update {
                    it.copy(chatRooms = chatList.chatRooms, isLoading = false, errorMessage = null)
                }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "채팅 목록 불러오기 오류")
                }
            }
    }

    /** 채팅방 메시지 불러오기 */
    fun loadChatMessages(chatRoomId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        chatRepository.getChatRoomMessages(chatRoomId)
            .onSuccess { result ->
                _uiState.update { current ->
                    current.copy(
                        chatRoomMessages = result.copy(
                            messages = result.messages.toList(),
                            chatRoom = result.chatRoom
                        ),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "메시지 불러오기 오류"
                    )
                }
            }
    }

    fun navigateToChatRoom(chatRoomId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ChattingScreen(chatRoomId))
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

    fun navigateToMatching(partnerId:Long)=viewModelScope.launch {
        navigator.navigate(FullScreen.MatchingDateScreen(myUserId.value,myUserRole.value,partnerId))
    }
}
