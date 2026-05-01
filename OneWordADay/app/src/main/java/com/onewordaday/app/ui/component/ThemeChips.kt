package com.onewordaday.app.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onewordaday.app.data.model.WordTheme
import com.onewordaday.app.ui.theme.Accent
import com.onewordaday.app.ui.theme.Surface
import com.onewordaday.app.ui.theme.ThemeChipBg

@Composable
fun ThemeChips(
    selectedTheme: WordTheme,
    onThemeSelected: (WordTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WordTheme.entries.forEach { theme ->
            val isSelected = theme == selectedTheme
            FilterChip(
                selected = isSelected,
                onClick = { onThemeSelected(theme) },
                label = {
                    Text(
                        text = theme.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = ThemeChipBg,
                    selectedContainerColor = Accent,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedLabelColor = Surface
                )
            )
        }
    }
}

@Composable
fun ThemeTag(theme: WordTheme, modifier: Modifier = Modifier) {
    FilterChip(
        selected = true,
        onClick = {},
        enabled = false,
        label = {
            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            disabledSelectedContainerColor = Accent.copy(alpha = 0.20f),
            disabledLabelColor = Accent
        ),
        modifier = modifier
    )
}
