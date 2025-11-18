package com.ssafy.yeobaeg.data.datasource.remote

import android.annotation.SuppressLint
import android.util.Log
import com.ssafy.yeobaeg.BuildConfig
import com.ssafy.yeobaeg.data.datasource.local.AuthDataSource
import com.ssafy.yeobaeg.data.model.ws.resquest.SendMessageRequest
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.HeartBeatTask
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ChatWebSocketService"

@Singleton
class ChatWebSocketService @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val wsUrl: String = BuildConfig.WS_URL
) {
    private var stompClient: StompClient? = null
    private var lifecycleDisposable: Disposable? = null
    private var globalSubscriptionDisposable: Disposable? = null
    private val subscriptions = mutableMapOf<Long, Disposable>()
    private val _chatMessageFlow = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 10
    )
    val chatMessageFlow = _chatMessageFlow.asSharedFlow()

    private val _globalEventFlow = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 10
    )
    val globalEventFlow = _globalEventFlow.asSharedFlow()


    private var isConnected = false
    fun initClient() {
        if (stompClient == null) {
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl).apply {
                withClientHeartbeat(300000)
                withServerHeartbeat(300000)
            }
            Log.d("ChatWebSocketService", "🧩 stompClient initialized")
        }
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun connect() {

        if (isConnected) {
            return
        }
        val accessToken = authDataSource.getAccessToken()
        val headers = listOf(StompHeader("Authorization", "Bearer $accessToken"))

        lifecycleDisposable = stompClient?.lifecycle()?.subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("ChatWebSocketService", "✅ WebSocket connected")
                    isConnected = true
                    Log.d(TAG, "connect: 전역 구독 호출")
                    //전역 구독 호출
                    coroutineScope.launch {
                        subscribeGlobal()
                    }

                }

                LifecycleEvent.Type.CLOSED -> {
                    Log.w("ChatWebSocketService", "❌ WebSocket closed")
                    isConnected = false
                }

                LifecycleEvent.Type.ERROR -> {
                    Log.e("ChatWebSocketService", "⚠️ WebSocket error: ${event.message}")
                    isConnected = false
                }

                else -> {}
            }
        }

        stompClient?.connect(headers)
    }

    //전역 구독
    suspend fun subscribeGlobal() {
        val userId = authDataSource.getUserId()

        val destination = "/sub/notifications/${userId}"
        Log.d(TAG, "📡 Attempting to subscribe global topic: $destination")

        try {
            globalSubscriptionDisposable = stompClient?.topic(destination)?.subscribe({ msg ->
                Log.d(TAG, "📢 Global notification: ${msg.payload}")
                _globalEventFlow.tryEmit(msg.payload)
            }, { e ->
                Log.w(
                    TAG,
                    "⚠️ Global subscription failed (expected if server not ready): ${e.message}"
                )
            })
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Global subscription not available: ${e.message}")
        }
    }

    //채팅방 구독
    fun subscribeToRoom(chatRoomId: Long) {
        Log.d(
            TAG,
            "🪄 subscribeToRoom called for chatRoomId=$chatRoomId (isConnected=$isConnected, stompClient=${stompClient != null})"
        )

        if (!isConnected || stompClient == null) {
            Log.w(TAG, "⚠️ Cannot subscribe - WebSocket not connected yet")
            return
        }

        if (subscriptions.containsKey(chatRoomId)) {
            Log.d(TAG, "⚠️ Already subscribed to room $chatRoomId, skipping")
            return
        }
        val destination = "/sub/chat/room/$chatRoomId"
        val disposable = stompClient?.topic(destination)?.subscribe({ msg ->
            Log.d("CHANNEL", "메시지 수신: ${msg.payload}")
            _chatMessageFlow.tryEmit(msg.payload)
        }, { e ->
            Log.e("ChatWebSocketService", "❌ Chat room subscription error: ${e.message}", e)
        })

        if (disposable != null) {
            subscriptions[chatRoomId] = disposable
            Log.d("ChatWebSocketService", "✅ Subscribed to room $chatRoomId")
        }
    }

    // 채팅방 구독 끊기
    fun unsubscribeFromRoom(chatRoomId: Long) {
        subscriptions[chatRoomId]?.dispose()
        subscriptions.remove(chatRoomId)
        Log.d("ChatWebSocketService", "🚪 Unsubscribed from room $chatRoomId")
    }

    // 메시지 송신
    fun sendMessage(request: SendMessageRequest) {
        if (!isConnected) {
            Log.w("ChatWebSocketService", "⚠️ sendMessage called before connection established")
            return
        }

        val jsonBody = Json.encodeToString(request)
        stompClient?.send("/pub/messages", jsonBody)?.subscribe({
            Log.d("ChatWebSocketService", "📤 Message sent successfully")
        }, { e ->
            Log.e("ChatWebSocketService", "❌ Send failed: ${e.message}", e)
        })?.let { disposable -> }
    }

    //연결 끊기
    fun disconnect() {
        lifecycleDisposable?.dispose()
        lifecycleDisposable = null
        subscriptions.values.forEach { it.dispose() }
        subscriptions.clear()
        stompClient?.disconnect()
        isConnected = false
        Log.d("ChatWebSocketService", "🔌 Disconnected WebSocket")
    }
}