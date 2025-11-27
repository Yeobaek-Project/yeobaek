package com.ssafy.yeobaeg.domain.model.ws

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventType: String,
    @SerialName("chatRoomUpdateSummary")
    val chatListUpdateSummary: ChatListUpdateSummary? = null
)