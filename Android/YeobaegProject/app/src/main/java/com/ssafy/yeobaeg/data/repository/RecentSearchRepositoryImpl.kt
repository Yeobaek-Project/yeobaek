package com.ssafy.yeobaeg.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ssafy.yeobaeg.di.StoreJson
import com.ssafy.yeobaeg.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val RECENTS_JSON = stringPreferencesKey("recent_searches_json")
private const val MAX_ITEMS = 10

class RecentSearchRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @StoreJson private val json: Json
) : RecentSearchRepository {

    override fun getRecentKeywords(): Flow<List<String>> =
        dataStore.data.map { it[RECENTS_JSON]?.let(::decode) ?: emptyList() }

    override suspend fun add(query: String) {
        val q = query.trim()
        if (q.isEmpty()) return
        dataStore.edit { prefs ->
            val cur = prefs[RECENTS_JSON]?.let(::decode) ?: emptyList()
            val updated = listOf(q) + cur.filterNot { it == q }
            prefs[RECENTS_JSON] = encode(updated.take(MAX_ITEMS))
        }
    }

    override suspend fun remove(query: String) {
        dataStore.edit { prefs ->
            val cur = prefs[RECENTS_JSON]?.let(::decode) ?: emptyList()
            prefs[RECENTS_JSON] = encode(cur.filterNot { it == query })
        }
    }

    override suspend fun clear() {
        dataStore.edit { it[RECENTS_JSON] = encode(emptyList()) }
    }

    private fun decode(s: String): List<String> =
        runCatching { json.decodeFromString(ListSerializer(String.serializer()), s) }.getOrElse { emptyList() }

    private fun encode(list: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), list)
}
