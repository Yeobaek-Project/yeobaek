package com.ssafy.yeobaeg.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.authDataStore

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferencesKeys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN]
        }.first()
    }

    fun getAccessTokenFlow(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ACCESS_TOKEN]
        }
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.REFRESH_TOKEN]
        }.first()
    }

    fun getRefreshTokenFlow(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.REFRESH_TOKEN]
        }
    }

    suspend fun saveUserId(userId: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId.toString()
        }
    }

    suspend fun getUserId(): Long? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID]?.toLongOrNull()
        }.first()
    }

    fun getUserIdFlow(): Flow<Long?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ID]?.toLongOrNull()
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.ACCESS_TOKEN)
            preferences.remove(PreferencesKeys.REFRESH_TOKEN)
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }
}

