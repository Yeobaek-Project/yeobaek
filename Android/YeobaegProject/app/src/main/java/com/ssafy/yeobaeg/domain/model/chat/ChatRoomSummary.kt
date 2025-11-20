package com.ssafy.yeobaeg.domain.model.chat


data class ChatRoomSummary (
    val chatRoomId:Long=0L,
    val partnerId:Long=0L,
    val partnerName:String="",
    val partnerImageUrl:String?="",
    val partnerRole: String="",
)