package com.onewordaday.app.ui.screen.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onewordaday.app.ui.component.ThemeTag
import com.onewordaday.app.ui.component.WordCard
import com.onewordaday.app.ui.theme.Background
import com.onewordaday.app.ui.theme.OnSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    onWordClick: (wordId: Long) -> Unit = {},
    viewModel: FavouritesViewModel = hiltViewModel()
) {
    val favourites by viewModel.favourites.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Favourites", style = MaterialTheme.typography.headlineMedium)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { paddingValues ->
        if (favourites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap ♡ on any word to save it here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(favourites, key = { it.id }) { word ->
                    ThemeTag(
                        theme = word.theme,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    WordCard(
                        word = word,
                        modifier = Modifier.fillMaxWidth(),
                        onFavouriteToggle = { viewModel.toggleFavourite(word) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
