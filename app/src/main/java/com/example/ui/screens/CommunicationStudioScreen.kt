package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.DistressLevel
import com.example.data.model.MessageSender
import com.example.data.model.Persona
import com.example.ui.components.AnimatedAudioWaveform
import com.example.ui.components.DynamicSupervisorCoachingCard
import com.example.ui.components.LiveAffectiveDistressMeter
import com.example.ui.theme.ClinicalAmberAccent
import com.example.ui.theme.ClinicalIndigoDark
import com.example.ui.theme.ClinicalTealDark
import com.example.ui.theme.DistressElevated
import com.example.ui.theme.DistressMild
import com.example.ui.theme.DistressModerate
import com.example.ui.theme.DistressSevere

@Composable
fun CommunicationStudioScreen(
    persona: Persona,
    messages: List<ChatMessage>,
    currentDistress: DistressLevel,
    activeDefense: String?,
    supervisorTip: String,
    isPatientThinking: Boolean,
    isSpeakingTts: Boolean,
    onSendMessage: (String) -> Unit,
    onSpeakLatest: () -> Unit,
    onStopSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    var studioInput by remember { mutableStateOf("") }
    val lastPatientMessage = messages.lastOrNull { it.sender == MessageSender.PATIENT }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Enlarged Character Stage
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalTealDark.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Character Avatar Circle with pulse
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(ClinicalTealDark.copy(alpha = 0.2f))
                        .border(2.dp, ClinicalTealDark.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Patient Stage",
                        tint = ClinicalTealDark,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${persona.name}, ${persona.age}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${persona.occupation} • ${persona.primaryDiagnosis}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Audio Waveforms
                AnimatedAudioWaveform(
                    isSpeaking = isSpeakingTts || isPatientThinking,
                    barCount = 24,
                    modifier = Modifier.height(50.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Voice Playback Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (isSpeakingTts) onStopSpeaking() else onSpeakLatest()
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeakingTts) Color(0xFFEF4444) else ClinicalTealDark
                        )
                    ) {
                        Icon(
                            imageVector = if (isSpeakingTts) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSpeakingTts) "Stop Audio Voice" else "Play Patient Voice (TTS)")
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Pitch: ${"%.2f".format(persona.speechPitch)}x | Rate: ${"%.2f".format(persona.speechRate)}x",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Live Affective Distress Meter
        LiveAffectiveDistressMeter(
            distressLevel = currentDistress,
            affectiveMarkers = persona.affectiveMarkers
        )

        // Dynamic Supervisor Coaching Card
        DynamicSupervisorCoachingCard(
            activeDefense = activeDefense,
            supervisorTip = supervisorTip
        )

        // Last Patient Verbal Statement Focus Card
        if (lastPatientMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LATEST VERBAL STATEMENT FROM ${persona.name.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (lastPatientMessage.activeDefense != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ClinicalAmberAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "[${lastPatientMessage.activeDefense}]",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = ClinicalAmberAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"${lastPatientMessage.text}\"",
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Communication Studio Clinical Response Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalTealDark.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "CLINICIAN RESPONSE INTERACTION",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = studioInput,
                    onValueChange = { studioInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Speak or type your clinical dialogue move...") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (studioInput.isNotBlank() && !isPatientThinking) {
                            val text = studioInput
                            studioInput = ""
                            onSendMessage(text)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = studioInput.isNotBlank() && !isPatientThinking,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ClinicalTealDark)
                ) {
                    if (isPatientThinking) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Patient Processing Affect...")
                    } else {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Deliver Response into Encounter")
                    }
                }
            }
        }
    }
}
