package com.onewordaday.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.onewordaday.app.data.model.WordTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class NotificationTime(val hour: Int, val minute: Int) {
    fun toDisplayString(): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val h = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "%d:%02d %s".format(h, minute, amPm)
    }
    fun toStorageString(): String = "%02d:%02d".format(hour, minute)
    companion object {
        fun fromStorageString(s: String): NotificationTime {
            val parts = s.split(":")
            return NotificationTime(parts[0].toInt(), parts[1].toInt())
        }
    }
}

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val store = context.dataStore

    private object Keys {
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIF_TIMES = listOf(
            stringPreferencesKey("notif_time_0"),
            stringPreferencesKey("notif_time_1"),
            stringPreferencesKey("notif_time_2"),
            stringPreferencesKey("notif_time_3"),
        )
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val LAST_MILESTONE_SHOWN = intPreferencesKey("last_milestone_shown")
    }

    private val defaultNotifTimes = listOf(
        NotificationTime(8, 0),
        NotificationTime(13, 0),
        NotificationTime(18, 0),
        NotificationTime(21, 0),
    )

    val selectedThemeFlow: Flow<WordTheme> = store.data.map { prefs ->
        WordTheme.fromKey(prefs[Keys.SELECTED_THEME] ?: WordTheme.GENERAL.key)
    }

    val notificationsEnabledFlow: Flow<Boolean> = store.data.map { prefs ->
        prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
    }

    val notificationTimesFlow: Flow<List<NotificationTime>> = store.data.map { prefs ->
        Keys.NOTIF_TIMES.mapIndexed { i, key ->
            prefs[key]?.let { NotificationTime.fromStorageString(it) } ?: defaultNotifTimes[i]
        }
    }

    suspend fun getSelectedTheme(): WordTheme {
        val prefs = store.data.first()
        return WordTheme.fromKey(prefs[Keys.SELECTED_THEME] ?: WordTheme.GENERAL.key)
    }

    suspend fun getNotificationTimes(): List<NotificationTime> {
        val prefs = store.data.first()
        return Keys.NOTIF_TIMES.mapIndexed { i, key ->
            prefs[key]?.let { NotificationTime.fromStorageString(it) } ?: defaultNotifTimes[i]
        }
    }

    suspend fun setSelectedTheme(theme: WordTheme) {
        store.edit { it[Keys.SELECTED_THEME] = theme.key }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        store.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setNotificationTime(slotIndex: Int, time: NotificationTime) {
        store.edit { it[Keys.NOTIF_TIMES[slotIndex]] = time.toStorageString() }
    }

    val isOnboardingCompleteFlow: Flow<Boolean> = store.data.map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    suspend fun setOnboardingComplete() {
        store.edit { it[Keys.ONBOARDING_COMPLETE] = true }
    }

    suspend fun getLastMilestoneShown(): Int {
        return store.data.first()[Keys.LAST_MILESTONE_SHOWN] ?: 0
    }

    suspend fun setLastMilestoneShown(streak: Int) {
        store.edit { it[Keys.LAST_MILESTONE_SHOWN] = streak }
    }
}
