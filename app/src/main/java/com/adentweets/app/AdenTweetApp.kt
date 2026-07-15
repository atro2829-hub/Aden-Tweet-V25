package com.adentweets.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class AdenTweetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        setArabicAsPrimaryLocale()
        createNotificationChannels()
    }

    private fun setArabicAsPrimaryLocale() {
        val arabicLocale = Locale("ar")
        Locale.setDefault(arabicLocale)
        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(arabicLocale)
            config.setLayoutDirection(arabicLocale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = arabicLocale
            @Suppress("DEPRECATION")
            config.setLayoutDirection(arabicLocale)
        }
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_MESSAGES,
                    "الرسائل",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "إشعارات الرسائل المباشرة"
                },
                NotificationChannel(
                    CHANNEL_NOTIFICATIONS,
                    "الإشعارات",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "الإشعارات الاجتماعية (إعجابات، متابعات، إلخ)"
                }
            )
            val manager = getSystemService(NotificationManager::class.java)
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    companion object {
        const val CHANNEL_MESSAGES = "channel_messages"
        const val CHANNEL_NOTIFICATIONS = "channel_notifications"
    }
}