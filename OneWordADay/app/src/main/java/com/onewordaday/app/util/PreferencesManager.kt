package com.onewordaday.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val store = context.dataStore

    private object Keys {
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val selectedThemeFlow: Flow<WordTheme> = store.data.map { prefs ->
        WordTheme.fromKey(prefs[Keys.SELECTED_THEME] ?: WordTheme.GENERAL.key)
    }

    val notificationsEnabledFlow: Flow<Boolean> = store.data.map { prefs ->
        prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun getSelectedTheme(): WordTheme {
        val prefs = store.data.first()
        return WordTheme.fromKey(prefs[Keys.SELECTED_THEME] ?: WordTheme.GENERAL.key)
    }

    suspend fun setSelectedTheme(theme: WordTheme) {
        store.edit { it[Keys.SELECTED_THEME] = theme.key }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        store.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }
}
