package com.ssafy.yeobaeg.domain.model

data class Pagination(
    val currentPage: Int=0,
    val pageSize: Int=0,
    val totalElements: Long=0L,
    val totalPages: Int=0,
    val hasNext: Boolean=false
)