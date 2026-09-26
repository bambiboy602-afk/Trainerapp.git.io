package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ClinicalTealDark

@Composable
fun AnimatedAudioWaveform(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    waveColor: Color = ClinicalTealDark,
    barCount: Int = 16
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val anim1 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = if (isSpeaking) 38f else 8f,
        animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar1"
    )
    val anim2 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = if (isSpeaking) 48f else 10f,
        animationSpec = infiniteRepeatable(tween(360, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar2"
    )
    val anim3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = if (isSpeaking) 54f else 8f,
        animationSpec = infiniteRepeatable(tween(510, delayMillis = 80, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar3"
    )
    val anim4 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = if (isSpeaking) 32f else 6f,
        animationSpec = infiniteRepeatable(tween(390, delayMillis = 120, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bar4"
    )

    val heights = listOf(
        anim1 * 0.4f, anim2 * 0.6f, anim3 * 0.8f, anim4,
        anim2, anim3, anim1 * 1.1f, anim4 * 1.2f,
        anim3, anim2, anim4, anim3 * 0.8f,
        anim2 * 0.7f, anim1 * 0.5f, anim4 * 0.4f, 6f
    )

    Row(
        modifier = modifier.height(60.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val barHeight = heights[i % heights.size].coerceIn(4f, 56f)
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(barHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isSpeaking) waveColor else waveColor.copy(alpha = 0.35f))
            )
        }
    }
}
