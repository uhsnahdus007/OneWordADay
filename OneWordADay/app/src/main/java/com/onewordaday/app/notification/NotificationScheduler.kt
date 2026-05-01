package com.onewordaday.app.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.onewordaday.app.util.DateUtils
import com.onewordaday.app.util.PreferencesManager
import com.onewordaday.app.worker.NotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    private val workManager get() = WorkManager.getInstance(context)

    private val slotTags = listOf(
        "notif_morning",
        "notif_afternoon",
        "notif_evening",
        "notif_night"
    )

    suspend fun scheduleAll() {
        val times = preferencesManager.getNotificationTimes()
        slotTags.zip(times).forEach { (tag, time) ->
            val delay = DateUtils.delayUntilTimeMs(time.hour, time.minute)
            val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .setInputData(workDataOf(NotificationWorker.KEY_SLOT to tag))
                .build()
            workManager.enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }

    fun cancelAll() {
        slotTags.forEach { workManager.cancelUniqueWork(it) }
    }
}
