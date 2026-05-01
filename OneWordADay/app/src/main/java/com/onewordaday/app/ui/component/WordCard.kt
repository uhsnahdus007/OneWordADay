package com.onewordaday.app.ui.component

import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.ui.theme.Accent
import com.onewordaday.app.ui.theme.Divider
import com.onewordaday.app.ui.theme.OnSurfaceVariant
import com.onewordaday.app.ui.theme.Surface
import java.util.Locale

@Composable
fun WordCard(
    word: Word,
    modifier: Modifier = Modifier,
    onFavouriteToggle: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val t = TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
        tts = t
        onDispose { t.stop(); t.shutdown() }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                }
                IconButton(
                    onClick = {
                        if (ttsReady) {
                            tts?.language = Locale.US
                            tts?.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, "tts_word")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Pronounce",
                        tint = OnSurfaceVariant
                    )
                }
                if (onFavouriteToggle != null) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onFavouriteToggle(word.isFavourited)
                        }
                    ) {
                        Icon(
                            imageVector = if (word.isFavourited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (word.isFavourited) "Unfavourite" else "Favourite",
                            tint = if (word.isFavourited) Accent else OnSurfaceVariant
                        )
                    }
                }
            }
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
