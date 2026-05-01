package com.onewordaday.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import com.onewordaday.app.notification.NotificationChannels
import com.onewordaday.app.notification.NotificationScheduler
import com.onewordaday.app.ui.navigation.AppNavigation
import com.onewordaday.app.ui.theme.OneWordADayTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    @Inject
    lateinit var notificationChannels: NotificationChannels

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) lifecycleScope.launch { notificationScheduler.scheduleAll() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        notificationChannels.createChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> lifecycleScope.launch { notificationScheduler.scheduleAll() }

                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            lifecycleScope.launch { notificationScheduler.scheduleAll() }
        }

        setContent {
            OneWordADayTheme {
                AppNavigation()
            }
        }
    }
}
