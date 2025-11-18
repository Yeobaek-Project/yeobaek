package com.ssafy.yeobaeg

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.yeobaeg.data.datasource.remote.ChatWebSocketService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@HiltAndroidApp
class YeobaegApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        super.onCreate()

        val entryPoint = EntryPointAccessors.fromApplication(this, WebSocketEntryPoint::class.java)
        entryPoint.chatWebSocketService().initClient()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    android.util.Log.w("FCM", "토큰 가져오기 실패", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                android.util.Log.d("FCM", "FCM 토큰: $token")
            }
    }

    override fun newImageLoader(): ImageLoader = imageLoader
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WebSocketEntryPoint {
    fun chatWebSocketService(): ChatWebSocketService
}
