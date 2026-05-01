package com.onewordaday.app.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.onewordaday.app.ui.theme.Surface

@Composable
fun WordCardSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "shimmer_alpha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            SkeletonLine(width = 0.55f, height = 48.dp, alpha = alpha)
            Spacer(Modifier.height(8.dp))
            SkeletonLine(width = 0.2f, height = 16.dp, alpha = alpha)
            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.fillMaxWidth().height(1.dp)
                    .background(Color.White.copy(alpha = alpha * 0.15f))
            )
            Spacer(Modifier.height(24.dp))
            SkeletonLine(width = 1f, height = 18.dp, alpha = alpha)
            Spacer(Modifier.height(10.dp))
            SkeletonLine(width = 0.85f, height = 18.dp, alpha = alpha)
            Spacer(Modifier.height(10.dp))
            SkeletonLine(width = 0.7f, height = 18.dp, alpha = alpha)
            Spacer(Modifier.height(24.dp))
            SkeletonLine(width = 0.25f, height = 16.dp, alpha = alpha)
            Spacer(Modifier.height(16.dp))
            SkeletonLine(width = 0.9f, height = 16.dp, alpha = alpha)
            Spacer(Modifier.height(10.dp))
            SkeletonLine(width = 0.75f, height = 16.dp, alpha = alpha)
        }
    }
}

@Composable
private fun SkeletonLine(width: Float, height: androidx.compose.ui.unit.Dp, alpha: Float) {
    Box(
        Modifier
            .fillMaxWidth(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = alpha * 0.12f))
    )
}
