// MyFirebaseMessagingService.kt
package com.ssafy.yeobaeg.fcm

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "새 FCM 토큰: $token")
        // 서버로 토큰 전달
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 1) notification payload
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]

        Log.d("FCM", "푸시 도착 (foreground 포함) data=${message.data}")

        PushNotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            body = body
        )
    }
}
