package com.ssafy.yeobaeg.domain.repository

import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    fun getRecentKeywords(): Flow<List<String>>
    suspend fun add(query: String)
    suspend fun remove(query: String)
    suspend fun clear()
}