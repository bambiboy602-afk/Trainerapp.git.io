package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DistressLevel
import com.example.ui.theme.DistressElevated
import com.example.ui.theme.DistressMild
import com.example.ui.theme.DistressModerate
import com.example.ui.theme.DistressSevere

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveAffectiveDistressMeter(
    distressLevel: DistressLevel,
    affectiveMarkers: List<String>,
    modifier: Modifier = Modifier
) {
    val targetColor = when (distressLevel) {
        DistressLevel.MILD -> DistressMild
        DistressLevel.MODERATE -> DistressModerate
        DistressLevel.ELEVATED -> DistressElevated
        DistressLevel.SEVERE -> DistressSevere
    }
    val animatedColor by animateColorAsState(targetValue = targetColor, animationSpec = tween(500), label = "color")

    val targetProgress = when (distressLevel) {
        DistressLevel.MILD -> 0.25f
        DistressLevel.MODERATE -> 0.50f
        DistressLevel.ELEVATED -> 0.75f
        DistressLevel.SEVERE -> 0.98f
    }
    val animatedProgress by animateFloatAsState(targetValue = targetProgress, animationSpec = tween(600), label = "progress")

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, animatedColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(animatedColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE AFFECTIVE DISTRESS METER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = animatedColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, animatedColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = distressLevel.label.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = animatedColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Segmented Meter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.3f)),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                DistressSegment(level = DistressLevel.MILD, current = distressLevel, color = DistressMild, modifier = Modifier.weight(1f))
                DistressSegment(level = DistressLevel.MODERATE, current = distressLevel, color = DistressModerate, modifier = Modifier.weight(1f))
                DistressSegment(level = DistressLevel.ELEVATED, current = distressLevel, color = DistressElevated, modifier = Modifier.weight(1f))
                DistressSegment(level = DistressLevel.SEVERE, current = distressLevel, color = DistressSevere, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Affective Markers Chips
            if (affectiveMarkers.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    affectiveMarkers.forEach { marker ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                                .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "• $marker",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DistressSegment(
    level: DistressLevel,
    current: DistressLevel,
    color: Color,
    modifier: Modifier = Modifier
) {
    val isActive = level.score <= current.score
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(if (isActive) color else color.copy(alpha = 0.15f))
    )
}
