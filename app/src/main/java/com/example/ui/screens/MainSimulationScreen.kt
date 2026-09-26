package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PersonaSelectorSheet
import com.example.ui.components.VisualClinicalAnalyticsView
import com.example.ui.dialogs.CustomPersonaDialog
import com.example.ui.dialogs.ExportRecordDialog
import com.example.ui.theme.ClinicalIndigoDark
import com.example.ui.theme.ClinicalTealDark
import com.example.ui.viewmodel.ClinicalViewModel

enum class NavigationTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    WORKSPACE("Workspace", Icons.Outlined.Forum),
    STUDIO("Studio", Icons.Outlined.RecordVoiceOver),
    DEBRIEF("Debrief", Icons.Outlined.Assessment),
    ANALYTICS("Analytics", Icons.Outlined.Analytics),
    EVIDENCE("Evidence", Icons.Outlined.Science),
    MI_GUIDE("MI Guide", Icons.Default.MenuBook)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSimulationScreen(
    viewModel: ClinicalViewModel = viewModel()
) {
    var currentTab by remember { mutableStateOf(NavigationTab.WORKSPACE) }
    var showPersonaSheet by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val allPersonas by viewModel.allPersonas.collectAsState()
    val selectedPersona by viewModel.selectedPersona.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isPatientThinking by viewModel.isPatientThinking.collectAsState()
    val turnLatency by viewModel.turnLatency.collectAsState()
    val currentDistress by viewModel.currentDistress.collectAsState()
    val activeDefense by viewModel.activeDefense.collectAsState()
    val supervisorTip by viewModel.supervisorTip.collectAsState()
    val sessionDurationSeconds by viewModel.sessionDurationSeconds.collectAsState()
    val emergingThemes by viewModel.emergingThemes.collectAsState()
    val isAnalyzingThemes by viewModel.isAnalyzingThemes.collectAsState()
    val debrief by viewModel.debrief.collectAsState()
    val isGeneratingDebrief by viewModel.isGeneratingDebrief.collectAsState()
    val isGeneratingSyntheticPersona by viewModel.isGeneratingSyntheticPersona.collectAsState()
    val isSpeakingTts by viewModel.ttsManager.isSpeaking.collectAsState()
    val literatureList by viewModel.literatureList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // BackHandler to return to Workspace if on secondary tab
    BackHandler(enabled = currentTab != NavigationTab.WORKSPACE) {
        currentTab = NavigationTab.WORKSPACE
    }

    val minutes = sessionDurationSeconds / 60
    val seconds = sessionDurationSeconds % 60
    val timerFormatted = "%02d:%02d".format(minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showPersonaSheet = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(ClinicalTealDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedPersona?.name ?: "Select Persona",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Persona",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = selectedPersona?.primaryDiagnosis?.substringBefore("(") ?: "Case Study",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    // Session Timer Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timerFormatted,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Reset Session
                    IconButton(
                        onClick = { viewModel.resetSession() },
                        modifier = Modifier.testTag("reset_session_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart Session")
                    }

                    // Export SOAP Record
                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.testTag("export_record_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Export Clinical Record")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ClinicalTealDark,
                            selectedTextColor = ClinicalTealDark,
                            indicatorColor = ClinicalTealDark.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val persona = selectedPersona
            if (persona != null) {
                when (currentTab) {
                    NavigationTab.WORKSPACE -> {
                        ClinicalWorkspaceScreen(
                            persona = persona,
                            messages = messages,
                            isPatientThinking = isPatientThinking,
                            turnLatency = turnLatency,
                            activeDefense = activeDefense,
                            supervisorTip = supervisorTip,
                            currentDistress = currentDistress,
                            sessionDurationSeconds = sessionDurationSeconds,
                            emergingThemes = emergingThemes,
                            isAnalyzingThemes = isAnalyzingThemes,
                            isSpeakingTts = isSpeakingTts,
                            onSendMessage = { viewModel.sendMessage(it) },
                            onAnalyzeThemes = { viewModel.analyzeThemes() },
                            onSpeakMessage = { viewModel.speakMessage(it) },
                            onStopSpeaking = { viewModel.stopSpeaking() }
                        )
                    }
                    NavigationTab.STUDIO -> {
                        CommunicationStudioScreen(
                            persona = persona,
                            messages = messages,
                            currentDistress = currentDistress,
                            activeDefense = activeDefense,
                            supervisorTip = supervisorTip,
                            isPatientThinking = isPatientThinking,
                            isSpeakingTts = isSpeakingTts,
                            onSendMessage = { viewModel.sendMessage(it) },
                            onSpeakLatest = {
                                messages.lastOrNull { it.sender == com.example.data.model.MessageSender.PATIENT }
                                    ?.let { viewModel.speakMessage(it) }
                            },
                            onStopSpeaking = { viewModel.stopSpeaking() }
                        )
                    }
                    NavigationTab.DEBRIEF -> {
                        DebriefScreen(
                            persona = persona,
                            debrief = debrief,
                            isGenerating = isGeneratingDebrief,
                            onGenerateDebrief = { viewModel.generateDebrief() }
                        )
                    }
                    NavigationTab.ANALYTICS -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            VisualClinicalAnalyticsView(messages = messages)
                        }
                    }
                    NavigationTab.EVIDENCE -> {
                        EvidenceSearchScreen(
                            literatureList = literatureList,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) }
                        )
                    }
                    NavigationTab.MI_GUIDE -> {
                        MiGuideScreen()
                    }
                }
            }
        }
    }

    // Persona Selector BottomSheet
    if (showPersonaSheet) {
        PersonaSelectorSheet(
            personas = allPersonas,
            selectedPersona = selectedPersona,
            onSelectPersona = { viewModel.selectPersona(it) },
            onOpenCustomDialog = { showCustomDialog = true },
            onDismiss = { showPersonaSheet = false }
        )
    }

    // Custom / AI Persona Generator Dialog
    if (showCustomDialog) {
        CustomPersonaDialog(
            isGeneratingAi = isGeneratingSyntheticPersona,
            onDismiss = { showCustomDialog = false },
            onGenerateAi = { prompt ->
                viewModel.generateSyntheticPersona(prompt) {
                    showCustomDialog = false
                }
            },
            onSaveManual = { newPersona ->
                viewModel.saveCustomPersona(newPersona)
                showCustomDialog = false
            }
        )
    }

    // Export SOAP Record Dialog
    if (showExportDialog) {
        ExportRecordDialog(
            recordText = viewModel.generateSoapRecordText(),
            onDismiss = { showExportDialog = false }
        )
    }
}
