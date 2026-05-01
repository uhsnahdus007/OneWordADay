package com.onewordaday.app.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.onewordaday.app.util.DateUtils
import com.onewordaday.app.worker.NotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager get() = WorkManager.getInstance(context)
    private data class NotifSlot(val tag: String, val hour: Int, val minute: Int)

    private val slots = listOf(
        NotifSlot("notif_morning",   8,  0),
        NotifSlot("notif_afternoon", 13, 0),
        NotifSlot("notif_evening",   18, 0),
        NotifSlot("notif_night",     21, 0)
    )

    fun scheduleAll() {
        slots.forEach { slot ->
            val delay = DateUtils.delayUntilTimeMs(slot.hour, slot.minute)
            val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(slot.tag)
                .setInputData(workDataOf(NotificationWorker.KEY_SLOT to slot.tag))
                .build()

            workManager.enqueueUniquePeriodicWork(
                slot.tag,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }

    fun cancelAll() {
        slots.forEach { workManager.cancelUniqueWork(it.tag) }
    }
}
