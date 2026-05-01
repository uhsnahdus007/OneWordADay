package com.onewordaday.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.onewordaday.app.MainActivity
import com.onewordaday.app.R
import com.onewordaday.app.data.model.Word
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val slotMessages = mapOf(
        "notif_morning" to "Your word of the day is ready. Tap to learn it.",
        "notif_afternoon" to "Lunchtime brain boost — have you used today's word?",
        "notif_evening" to "Evening check-in — try using today's word in a sentence.",
        "notif_night" to "Last chance — review today's word before bed."
    )

    fun showWordNotification(word: Word, slot: String, notificationId: Int = slot.hashCode()) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("FROM_NOTIFICATION", true)
            putExtra("NOTIFICATION_SLOT", slot)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = slotMessages[slot] ?: "Practice your word of the day!"

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(word.word.replaceFirstChar { it.uppercase() })
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${word.word.replaceFirstChar { it.uppercase() }} (${word.partOfSpeech})\n$message"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}
