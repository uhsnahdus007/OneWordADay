package com.onewordaday.app.ui.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onewordaday.app.ui.component.ThemeTag
import com.onewordaday.app.ui.component.WordCard
import com.onewordaday.app.ui.component.WordCardSkeleton
import com.onewordaday.app.ui.theme.Accent
import com.onewordaday.app.ui.theme.Background
import com.onewordaday.app.ui.theme.OnSurfaceVariant
import com.onewordaday.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val milestoneStreak by viewModel.milestoneStreak.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    // Milestone celebration dialog
    if (milestoneStreak > 0) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMilestone() },
            title = { Text("$milestoneStreak-day streak!") },
            text = {
                Text(
                    text = streakMessage(milestoneStreak),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissMilestone() },
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Keep going!") }
            }
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = DateUtils.formatForDisplay(DateUtils.today()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                },
                actions = {
                    if (streak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "🔥 $streak",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Accent
                            )
                        }
                    }
                    val successState = uiState as? HomeUiState.Success
                    if (successState != null) {
                        IconButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.shareCurrentWord()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = OnSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    (fadeIn() + slideInVertically { it / 8 }) togetherWith fadeOut()
                },
                label = "home_content"
            ) { state ->
                when (state) {
                    is HomeUiState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            WordCardSkeleton(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    is HomeUiState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            ThemeTag(
                                theme = state.word.theme,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            WordCard(
                                word = state.word,
                                modifier = Modifier.fillMaxWidth(),
                                onFavouriteToggle = { viewModel.toggleFavourite() }
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Try using this word in a conversation today.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    is HomeUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Something went wrong",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun streakMessage(streak: Int): String = when (streak) {
    3 -> "3 days in a row! You're building a habit. Keep it up!"
    7 -> "One full week! Your vocabulary is growing fast."
    14 -> "Two weeks strong! Most people give up by now — you didn't."
    30 -> "30 days! A true word enthusiast. Incredible dedication."
    60 -> "60 days! You're in the top 1% of learners."
    100 -> "100 DAYS! Legendary. You've mastered the art of consistency."
    else -> "Amazing streak! Keep the momentum going!"
}
