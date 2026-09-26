package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClinicalAmberAccent
import com.example.ui.theme.ClinicalIndigoDark
import com.example.ui.theme.ClinicalTealDark
import com.example.ui.theme.DistressElevated
import com.example.ui.theme.DistressMild
import com.example.ui.theme.SupervisorPurple

@Composable
fun MiGuideScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PACE Philosophy Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalTealDark.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ClinicalTealDark.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Handshake,
                            contentDescription = null,
                            tint = ClinicalTealDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "THE PACE SPIRIT OF MOTIVATIONAL INTERVIEWING",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Relational foundation for counseling and peer support",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PacePill(letter = "P", title = "Partnership", desc = "Collaborative dance, not wrestling", color = ClinicalTealDark, modifier = Modifier.weight(1f))
                    PacePill(letter = "A", title = "Acceptance", desc = "Unconditional positive regard & autonomy", color = ClinicalIndigoDark, modifier = Modifier.weight(1f))
                    PacePill(letter = "C", title = "Compassion", desc = "Actively promoting client welfare", color = ClinicalAmberAccent, modifier = Modifier.weight(1f))
                    PacePill(letter = "E", title = "Evocation", desc = "Drawing motivation from within client", color = SupervisorPurple, modifier = Modifier.weight(1f))
                }
            }
        }

        // OARS Framework Breakdown
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.QuestionAnswer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "THE OARS CLINICAL COMMUNICATION FRAMEWORK",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OarsGuideCard(
                    title = "O — Open-Ended Questions",
                    purpose = "Invites narrative exploration and prevents defensive yes/no interrogations.",
                    doExample = "\"What do you think would happen if you took a 15-minute break when the anger spikes?\"",
                    dontExample = "\"Do you realize that your drinking is ruining your marriage?\""
                )

                Spacer(modifier = Modifier.height(10.dp))

                OarsGuideCard(
                    title = "A — Affirmations",
                    purpose = "Validates internal strengths, values, and past survival endurance.",
                    doExample = "\"You've carried this heavy weight all season while continuing to show up for your athletes every day.\"",
                    dontExample = "\"I'm proud of you.\" (Keeps authority in clinician's court rather than client's)"
                )

                Spacer(modifier = Modifier.height(10.dp))

                OarsGuideCard(
                    title = "R — Complex Reflections",
                    purpose = "Hypothesizes underlying emotional meaning, holding up an accurate mirror without judgment.",
                    doExample = "\"On one hand, maintaining total control protects you from failure; on the other, the physical cost is becoming unbearable.\"",
                    dontExample = "\"So you have an anxiety disorder.\" (Reductive clinical labeling)"
                )

                Spacer(modifier = Modifier.height(10.dp))

                OarsGuideCard(
                    title = "S — Summaries",
                    purpose = "Pulls together ambivalence, transition points, and change talk to consolidate momentum.",
                    doExample = "\"Let me make sure I'm hearing you: You don't want pills, but you are ready to stop waking up at 3 A.M. soaked in sweat.\"",
                    dontExample = "\"Here is what you must do next week.\""
                )
            }
        }

        // Rolling with Resistance
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Shield, contentDescription = null, tint = ClinicalAmberAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ROLLING WITH RESISTANCE & DEFENSES",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ResistanceStrategyCard(
                    name = "Simple Reflection",
                    tip = "Mirror the resistance directly without arguing: \"You're not convinced this conversation is worth your time.\""
                )
                Spacer(modifier = Modifier.height(8.dp))

                ResistanceStrategyCard(
                    name = "Double-Sided Reflection",
                    tip = "Capture both sides of the ambivalence: \"You love the flow of manic art sprints, AND the subsequent depressive drop is terrifying.\""
                )
                Spacer(modifier = Modifier.height(8.dp))

                ResistanceStrategyCard(
                    name = "Emphasizing Personal Autonomy",
                    tip = "Remind client they hold the controls: \"Nobody can force you to take medication. You are the expert on your life.\""
                )
            }
        }

        // B.A.M.B.I. Recursive Stabilization Model (RSM) - 5 Boundary Failures
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, SupervisorPurple.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "B.A.M.B.I. RECURSIVE RECOVERY & 5 BOUNDARY FAILURES",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SupervisorPurple
                )
                Text(
                    text = "Lived-experience street recovery framework by Bambi (Phoenix, AZ) & Tom curb mentorship",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoundaryCard("1. Relief Failure (Pain → Relief)", "Client has only one relief valve (drugs, benders, self-sabotage). Expand alternative paths; delay impulse by 15 mins.")
                Spacer(modifier = Modifier.height(6.dp))
                BoundaryCard("2. Meaning Failure (Event → Stuck Interpretation)", "The trauma ended, but stuck interpretation remains active in nervous system. Separate past events from present leverage.")
                Spacer(modifier = Modifier.height(6.dp))
                BoundaryCard("3. Prediction Failure (Unknown → Catastrophe)", "Uncertainty is automatically computed as catastrophic collapse. Sensory grounding: Focus strictly on the next 60 minutes.")
                Spacer(modifier = Modifier.height(6.dp))
                BoundaryCard("4. Connection Failure (Need → Isolation / Defense)", "Deep emotional need exists, but pathways are locked. Lower the drawbridge with low-stakes micro-interactions.")
                Spacer(modifier = Modifier.height(6.dp))
                BoundaryCard("5. Identity Failure (Mistake → Self-Definition)", "\"Failure is an event, but it started wearing your name tag. Drop the bag.\" Separate behavior from core human dignity.")
            }
        }
    }
}

@Composable
private fun PacePill(letter: String, title: String, desc: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = letter, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun OarsGuideCard(title: String, purpose: String, doExample: String, dontExample: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Text(text = purpose, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = null, tint = DistressMild, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "DO: $doExample", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = null, tint = DistressElevated, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "DON'T: $dontExample", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ResistanceStrategyCard(name: String, tip: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, ClinicalAmberAccent.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = name, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = ClinicalAmberAccent)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun BoundaryCard(title: String, desc: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, SupervisorPurple.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SupervisorPurple)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
