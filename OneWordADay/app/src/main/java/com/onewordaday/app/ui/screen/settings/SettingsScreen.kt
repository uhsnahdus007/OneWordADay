package com.onewordaday.app.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onewordaday.app.BuildConfig
import com.onewordaday.app.data.model.WordTheme
import com.onewordaday.app.ui.component.ThemeChips
import com.onewordaday.app.ui.theme.Accent
import com.onewordaday.app.ui.theme.AccentDim
import com.onewordaday.app.ui.theme.Background
import com.onewordaday.app.ui.theme.Divider
import com.onewordaday.app.ui.theme.OnSurface
import com.onewordaday.app.ui.theme.OnSurfaceVariant
import com.onewordaday.app.ui.theme.Surface
import com.onewordaday.app.ui.theme.SurfaceVariant

private val slotLabels = listOf("Morning", "Afternoon", "Evening", "Night")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val notificationTimes by viewModel.notificationTimes.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    var showTimePickerForSlot by remember { mutableIntStateOf(-1) }

    if (showTimePickerForSlot >= 0) {
        val slot = showTimePickerForSlot
        val current = notificationTimes.getOrNull(slot)
        val timePickerState = rememberTimePickerState(
            initialHour = current?.hour ?: 8,
            initialMinute = current?.minute ?: 0,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePickerForSlot = -1 },
            title = { Text("Set ${slotLabels.getOrElse(slot) { "Notification" }} time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setNotificationTime(slot, timePickerState.hour, timePickerState.minute)
                    showTimePickerForSlot = -1
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerForSlot = -1 }) { Text("Cancel") }
            },
            containerColor = Surface
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // — Word Theme —
            Text(
                text = "Word Theme",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Takes effect from tomorrow's word",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            ThemeChips(
                selectedTheme = selectedTheme,
                onThemeSelected = { theme ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.setTheme(theme)
                }
            )

            Spacer(Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // — Daily Reminders —
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Reminders",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Notifications to practice your word",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = viewModel::setNotificationsEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Background,
                        checkedTrackColor = Accent
                    )
                )
            }

            if (notificationsEnabled) {
                Spacer(Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        notificationTimes.forEachIndexed { index, time ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = slotLabels.getOrElse(index) { "Slot $index" },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = OnSurface
                                    )
                                    Text(
                                        text = time.toDisplayString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Accent
                                    )
                                }
                                IconButton(onClick = { showTimePickerForSlot = index }) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Edit time",
                                        tint = OnSurfaceVariant
                                    )
                                }
                            }
                            if (index < notificationTimes.lastIndex) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .padding(horizontal = 12.dp)
                                        .background(Divider)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // — Stats —
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurface
            )
            Spacer(Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(value = stats.totalSeen.toString(), label = "Words\nLearned")
                    StatDivider()
                    StatItem(value = stats.currentStreak.toString(), label = "Current\nStreak")
                    StatDivider()
                    StatItem(value = stats.bestStreak.toString(), label = "Best\nStreak")
                    StatDivider()
                    StatItem(value = stats.favouritesCount.toString(), label = "Favourites")
                }
            }

            Spacer(Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // — About —
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "OneWordADay",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Accent
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "v${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "200 words · 6 themes · offline-first",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Divider)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Created by",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "Sudhanshu Shekhar",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HorizontalDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Divider)
    )
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Accent
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        Modifier
            .height(40.dp)
            .width(1.dp)
            .background(Divider)
    )
}
