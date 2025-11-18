package com.ssafy.yeobaeg.domain.model.chat

data class WriteReview (
    val exhibitionId:Long=0L,
    val reviewId:Long=0L,
    var rating:Int=0,
    var comment:String=""
)