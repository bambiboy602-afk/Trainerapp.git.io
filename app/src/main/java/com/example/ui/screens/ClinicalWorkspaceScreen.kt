package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.DistressLevel
import com.example.data.model.EmergingThemesReport
import com.example.data.model.MessageSender
import com.example.data.model.Persona
import com.example.ui.components.AnimatedAudioWaveform
import com.example.ui.components.CaseDossierPanel
import com.example.ui.components.DynamicSupervisorCoachingCard
import com.example.ui.theme.ClinicalAmberAccent
import com.example.ui.theme.ClinicalIndigoDark
import com.example.ui.theme.ClinicalTealDark
import com.example.ui.theme.DistressElevated
import com.example.ui.theme.DistressMild
import com.example.ui.theme.DistressModerate
import com.example.ui.theme.DistressSevere
import com.example.ui.theme.SupervisorPurple

@Composable
fun ClinicalWorkspaceScreen(
    persona: Persona,
    messages: List<ChatMessage>,
    isPatientThinking: Boolean,
    turnLatency: Long,
    activeDefense: String?,
    supervisorTip: String,
    currentDistress: DistressLevel,
    sessionDurationSeconds: Long,
    emergingThemes: EmergingThemesReport?,
    isAnalyzingThemes: Boolean,
    isSpeakingTts: Boolean,
    onSendMessage: (String) -> Unit,
    onAnalyzeThemes: () -> Unit,
    onSpeakMessage: (ChatMessage) -> Unit,
    onStopSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showDossierPanel by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isPatientThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickSuggestions = listOf(
        "\"What do you think would happen if...\"",
        "\"It sounds like that requires immense energy.\"",
        "\"On one hand it protects you, on the other...\"",
        "\"I notice tension in your chest right now.\"",
        "\"What would honoring your dignity look like?\""
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Workspace Status Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val distressColor = when (currentDistress) {
                        DistressLevel.MILD -> DistressMild
                        DistressLevel.MODERATE -> DistressModerate
                        DistressLevel.ELEVATED -> DistressElevated
                        DistressLevel.SEVERE -> DistressSevere
                    }

                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(distressColor))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Distress: ${currentDistress.label}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = distressColor
                    )

                    if (turnLatency > 0) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "• Latency: ${turnLatency}ms",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Toggle Dossier Panel
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (showDossierPanel) ClinicalIndigoDark.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (showDossierPanel) ClinicalIndigoDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.clickable { showDossierPanel = !showDossierPanel }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FolderShared,
                            contentDescription = "Toggle Dossier",
                            tint = if (showDossierPanel) ClinicalIndigoDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showDossierPanel) "Hide Dossier" else "Case Dossier",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (showDossierPanel) ClinicalIndigoDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (showDossierPanel) {
            // Collapsible Dossier View
            CaseDossierPanel(
                persona = persona,
                emergingThemes = emergingThemes,
                isAnalyzingThemes = isAnalyzingThemes,
                onAnalyzeThemes = onAnalyzeThemes,
                modifier = Modifier.weight(1f)
            )
        } else {
            // Main Dialogue Area
            Column(modifier = Modifier.weight(1f)) {
                // Dynamic Supervisor Coaching Strip
                DynamicSupervisorCoachingCard(
                    activeDefense = activeDefense,
                    supervisorTip = supervisorTip,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )

                // Message List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        DialogueMessageItem(
                            message = msg,
                            persona = persona,
                            isSpeaking = isSpeakingTts,
                            onSpeak = { onSpeakMessage(msg) },
                            onStop = onStopSpeaking
                        )
                    }

                    if (isPatientThinking) {
                        item {
                            PatientThinkingIndicator(persona = persona)
                        }
                    }
                }

                // Quick Response Suggestions Row
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickSuggestions) { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable {
                                inputText = suggestion.removeSurrounding("\"")
                            }
                        ) {
                            Text(
                                text = suggestion,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Input Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("clinician_input_field"),
                            placeholder = { Text("Formulate your clinical inquiry or reflection...") },
                            maxLines = 4,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ClinicalTealDark,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank() && !isPatientThinking) {
                                    val textToSend = inputText
                                    inputText = ""
                                    onSendMessage(textToSend)
                                }
                            },
                            enabled = inputText.isNotBlank() && !isPatientThinking,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank()) ClinicalTealDark else MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message",
                                tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogueMessageItem(
    message: ChatMessage,
    persona: Persona,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onStop: () -> Unit
) {
    val isClinician = message.sender == MessageSender.CLINICIAN

    val distressColor = when (message.distressLevel) {
        DistressLevel.MILD -> DistressMild
        DistressLevel.MODERATE -> DistressModerate
        DistressLevel.ELEVATED -> DistressElevated
        DistressLevel.SEVERE -> DistressSevere
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isClinician) Alignment.End else Alignment.Start
    ) {
        // Speaker Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isClinician) "Clinician (You)" else persona.name,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isClinician) ClinicalTealDark else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isClinician && message.activeDefense != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ClinicalAmberAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "[${message.activeDefense}]",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = ClinicalAmberAccent
                    )
                }
            }

            if (!isClinician) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(distressColor))
            }
        }

        // Bubble Content
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isClinician) 16.dp else 4.dp,
                bottomEnd = if (isClinician) 4.dp else 16.dp
            ),
            color = if (isClinician) ClinicalTealDark.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isClinician) ClinicalTealDark.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!isClinician) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Turn latency: ${message.latencyMs}ms",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Play Audio",
                                tint = ClinicalTealDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientThinkingIndicator(persona: Persona) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(ClinicalTealDark.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HourglassTop,
                contentDescription = null,
                tint = ClinicalTealDark,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${persona.name} is reflecting...",
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(10.dp))
        AnimatedAudioWaveform(isSpeaking = true, barCount = 8)
    }
}
