package com.onewordaday.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.onewordaday.app.AppViewModel
import com.onewordaday.app.ui.screen.detail.WordDetailScreen
import com.onewordaday.app.ui.screen.favourites.FavouritesScreen
import com.onewordaday.app.ui.screen.history.HistoryScreen
import com.onewordaday.app.ui.screen.home.HomeScreen
import com.onewordaday.app.ui.screen.onboarding.OnboardingScreen
import com.onewordaday.app.ui.screen.settings.SettingsScreen
import com.onewordaday.app.ui.theme.Accent
import com.onewordaday.app.ui.theme.Background
import com.onewordaday.app.ui.theme.Divider
import com.onewordaday.app.ui.theme.OnSurfaceVariant
import com.onewordaday.app.ui.theme.Surface

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object History : Screen("history", "History", Icons.Default.History)
    data object Favourites : Screen("favourites", "Favourites", Icons.Default.Favorite)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

private val bottomNavItems = listOf(Screen.Home, Screen.History, Screen.Favourites, Screen.Settings)

@Composable
fun AppNavigation(appViewModel: AppViewModel = hiltViewModel()) {
    val isOnboardingComplete by appViewModel.isOnboardingComplete.collectAsStateWithLifecycle()

    // null = still loading from DataStore — show nothing until resolved
    if (isOnboardingComplete == null) return

    if (isOnboardingComplete == false) {
        OnboardingScreen(onFinish = { appViewModel.completeOnboarding() })
        return
    }

    MainNavigation()
}

@Composable
private fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(screen.icon, contentDescription = screen.label)
                            },
                            label = { Text(screen.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                unselectedIconColor = OnSurfaceVariant,
                                unselectedTextColor = OnSurfaceVariant,
                                indicatorColor = Divider
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.History.route) {
                HistoryScreen(onWordClick = { wordId ->
                    navController.navigate("word/$wordId")
                })
            }
            composable(Screen.Favourites.route) {
                FavouritesScreen(onWordClick = { wordId ->
                    navController.navigate("word/$wordId")
                })
            }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(
                route = "word/{wordId}",
                arguments = listOf(navArgument("wordId") { type = NavType.LongType })
            ) {
                WordDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
