package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.DistressLevel
import com.example.data.model.MessageSender
import com.example.ui.theme.ClinicalAmberAccent
import com.example.ui.theme.ClinicalTealDark
import com.example.ui.theme.DistressElevated
import com.example.ui.theme.DistressMild
import com.example.ui.theme.DistressModerate
import com.example.ui.theme.DistressSevere

@Composable
fun VisualClinicalAnalyticsView(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    val patientMessages = messages.filter { it.sender == MessageSender.PATIENT }
    val clinicianMessages = messages.filter { it.sender == MessageSender.CLINICIAN }

    val clinicianWordCount = clinicianMessages.sumOf { it.text.split("\\s+".toRegex()).size }
    val patientWordCount = patientMessages.sumOf { it.text.split("\\s+".toRegex()).size }
    val totalWords = (clinicianWordCount + patientWordCount).coerceAtLeast(1)
    val clinicianRatio = clinicianWordCount.toFloat() / totalWords
    val patientRatio = patientWordCount.toFloat() / totalWords

    val resistanceEvents = patientMessages.count { it.activeDefense != null }
    val rapportEvents = patientMessages.count { it.activeDefense == null && it.distressLevel != DistressLevel.SEVERE }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart 1: Distress Curve
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "EMOTIONAL DISTRESS TRAJECTORY CURVE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Session turns vs Inferred patient affective distress score",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                DistressCurveCanvas(patientMessages = patientMessages)
            }
        }

        // Chart 2: Talk-Time Balance
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TALK-TIME BALANCE (WORD COUNT RATIO)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Optimal MI guideline: Clinician speaks <40%, Patient speaks >60%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Ratio Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(clinicianRatio.coerceAtLeast(0.05f))
                            .height(18.dp)
                            .background(ClinicalTealDark)
                    )
                    Box(
                        modifier = Modifier
                            .weight(patientRatio.coerceAtLeast(0.05f))
                            .height(18.dp)
                            .background(ClinicalAmberAccent)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ClinicalTealDark))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Clinician: ${"%.1f".format(clinicianRatio * 100)}% ($clinicianWordCount w)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ClinicalAmberAccent))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Patient: ${"%.1f".format(patientRatio * 100)}% ($patientWordCount w)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Metric 3: Resistance vs Rapport Events
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CLINICAL EVENT FREQUENCY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, DistressElevated.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$resistanceEvents",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = DistressElevated
                            )
                            Text(
                                text = "Resistance Events",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, DistressMild.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$rapportEvents",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = DistressMild
                            )
                            Text(
                                text = "Rapport Openings",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DistressCurveCanvas(patientMessages: List<ChatMessage>) {
    val scores = if (patientMessages.isEmpty()) {
        listOf(50f)
    } else {
        patientMessages.map { it.distressLevel.score.toFloat() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // Y-axis grid lines (Severe: 95, Elevated: 75, Moderate: 50, Mild: 25)
            val levels = listOf(0.1f, 0.35f, 0.65f, 0.9f)
            levels.forEach { yRatio ->
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(0f, h * yRatio),
                    end = Offset(w, h * yRatio),
                    strokeWidth = 1f
                )
            }

            if (scores.size == 1) {
                val y = h * (1f - (scores.first() / 100f))
                drawCircle(color = DistressModerate, radius = 6.dp.toPx(), center = Offset(w / 2f, y))
                return@Canvas
            }

            val stepX = w / (scores.size - 1)
            val path = Path()
            val fillPath = Path()

            scores.forEachIndexed { i, score ->
                val x = i * stepX
                val y = (h * (1f - (score / 100f))).coerceIn(10f, h - 10f)

                if (i == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, h)
                    fillPath.lineTo(x, y)
                } else {
                    val prevX = (i - 1) * stepX
                    val prevY = (h * (1f - (scores[i - 1] / 100f))).coerceIn(10f, h - 10f)
                    val cX1 = (prevX + x) / 2
                    path.cubicTo(cX1, prevY, cX1, y, x, y)
                    fillPath.cubicTo(cX1, prevY, cX1, y, x, y)
                }

                if (i == scores.size - 1) {
                    fillPath.lineTo(x, h)
                    fillPath.close()
                }
            }

            // Fill gradient under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(ClinicalTealDark.copy(alpha = 0.3f), Color.Transparent),
                    startY = 0f,
                    endY = h
                )
            )

            // Draw line
            drawPath(
                path = path,
                color = ClinicalTealDark,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Points
            scores.forEachIndexed { i, score ->
                val x = i * stepX
                val y = (h * (1f - (score / 100f))).coerceIn(10f, h - 10f)
                val dotColor = when {
                    score >= 85f -> DistressSevere
                    score >= 65f -> DistressElevated
                    score >= 40f -> DistressModerate
                    else -> DistressMild
                }
                drawCircle(color = Color.Black, radius = 5.dp.toPx(), center = Offset(x, y))
                drawCircle(color = dotColor, radius = 4.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}
