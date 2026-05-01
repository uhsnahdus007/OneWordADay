package com.onewordaday.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onewordaday.app.data.model.WordTheme
import com.onewordaday.app.data.repository.WordRepository
import com.onewordaday.app.notification.NotificationScheduler
import com.onewordaday.app.util.NotificationTime
import com.onewordaday.app.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppStats(
    val totalSeen: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val favouritesCount: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val notificationScheduler: NotificationScheduler,
    private val repository: WordRepository
) : ViewModel() {

    val selectedTheme: StateFlow<WordTheme> = preferencesManager.selectedThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WordTheme.GENERAL)

    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val notificationTimes: StateFlow<List<NotificationTime>> = preferencesManager.notificationTimesFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            listOf(
                NotificationTime(8, 0),
                NotificationTime(13, 0),
                NotificationTime(18, 0),
                NotificationTime(21, 0)
            )
        )

    private val _stats = MutableStateFlow(AppStats())
    val stats: StateFlow<AppStats> = _stats

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _stats.value = AppStats(
                totalSeen = repository.getTotalWordsSeen(),
                currentStreak = repository.getCurrentStreak(),
                bestStreak = repository.getBestStreak(),
                favouritesCount = repository.getFavouritesCount()
            )
        }
    }

    fun setTheme(theme: WordTheme) {
        viewModelScope.launch {
            preferencesManager.setSelectedTheme(theme)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            if (enabled) notificationScheduler.scheduleAll()
            else notificationScheduler.cancelAll()
        }
    }

    fun setNotificationTime(slotIndex: Int, hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesManager.setNotificationTime(slotIndex, NotificationTime(hour, minute))
            if (notificationsEnabled.value) notificationScheduler.scheduleAll()
        }
    }
}
