package com.ssafy.yeobaeg.domain.model.chat


data class ChatRoomInfo (
    val chatRoomId:Long,
    val partnerId:Long,
    val partnerName:String,
    val partnerImageUrl:String?,
    val partnerRole: String,
    val unreadCount:Int?,
    val lastMessage:String?,
    val lastMessageType:String?,
    val lastMessageAt:String?
)