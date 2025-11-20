package com.ssafy.yeobaeg.data.model.chat.request

import kotlinx.serialization.Serializable

@Serializable
data class WriteReviewRequest (
    val exhibitionId:Long,
    val revieweeId:Long,
    val rating:Int,
    val comment:String
)