package com.onewordaday.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onewordaday.app.data.model.WordTheme
import com.onewordaday.app.notification.NotificationScheduler
import com.onewordaday.app.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val selectedTheme: StateFlow<WordTheme> = preferencesManager.selectedThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WordTheme.GENERAL)

    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

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
}
