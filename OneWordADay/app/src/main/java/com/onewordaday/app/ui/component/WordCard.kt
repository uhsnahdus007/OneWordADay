package com.onewordaday.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.ui.theme.Accent
import com.onewordaday.app.ui.theme.Divider
import com.onewordaday.app.ui.theme.OnSurfaceVariant
import com.onewordaday.app.ui.theme.Surface

@Composable
fun WordCard(word: Word, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = word.word,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = word.partOfSpeech,
                style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                color = OnSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Divider)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = word.definition,
                style = MaterialTheme.typography.bodyLarge
            )
            if (word.examples.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Examples",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = OnSurfaceVariant
                    )
                )
                Spacer(Modifier.height(12.dp))
                word.examples.forEach { example ->
                    ExampleItem(example)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun ExampleItem(text: String) {
    Row {
        Box(
            Modifier
                .width(3.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Accent)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = "“$text”",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic
        )
    }
}
