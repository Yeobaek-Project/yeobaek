package com.ssafy.yeobaeg.data.model.ws.response

import com.ssafy.yeobaeg.domain.model.ws.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val eventType: String,
    @SerialName("chatRoomUpdateSummary")
    val chatListUpdateSummaryResponse: ChatListUpdateSummaryResponse? = null
)

fun EventResponse.toDomain(): Event{
    return Event(
        eventType=eventType,
        chatListUpdateSummary= chatListUpdateSummaryResponse?.toDomain()
    )
}