package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DefenseMechanism
import com.example.data.model.DistressLevel
import com.example.data.model.Persona
import com.example.ui.theme.ClinicalIndigoDark
import com.example.ui.theme.ClinicalTealDark

@Composable
fun CustomPersonaDialog(
    isGeneratingAi: Boolean,
    onDismiss: () -> Unit,
    onGenerateAi: (prompt: String) -> Unit,
    onSaveManual: (Persona) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var aiPrompt by remember { mutableStateOf("") }

    // Manual fields
    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("30") }
    var occupation by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var dsmCode by remember { mutableStateOf("DSM-5-TR: ") }
    var formulation by remember { mutableStateOf("") }
    var defenseName by remember { mutableStateOf("") }
    var defenseManifestation by remember { mutableStateOf("") }
    var culturalBarriers by remember { mutableStateOf("") }
    var careAmbivalence by remember { mutableStateOf("") }
    var openingStatement by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOM & SYNTHETIC PERSONA STUDIO",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Generator")
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Manual Builder")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // AI Generation Tab
                    Text(
                        text = "Generate a comprehensive clinical vignette with authentic defense mechanisms, cultural nuances, and care barriers via Gemini 3.5 Flash.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = { aiPrompt = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Clinical Case Description") },
                        placeholder = { Text("e.g. A 45-year-old emergency room nurse experiencing acute burnout, emotional numbing, and marital conflict...") },
                        minLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (aiPrompt.isNotBlank()) {
                                onGenerateAi(aiPrompt.trim())
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = aiPrompt.isNotBlank() && !isGeneratingAi,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalIndigoDark)
                    ) {
                        if (isGeneratingAi) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Synthetic Clinical Persona...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Clinical Persona")
                        }
                    }
                } else {
                    // Manual Builder Tab
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { ageText = it },
                            label = { Text("Age") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = occupation,
                            onValueChange = { occupation = it },
                            label = { Text("Occupation") },
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = { diagnosis = it },
                        label = { Text("Primary Diagnosis") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formulation,
                        onValueChange = { formulation = it },
                        label = { Text("DSM-5 Formulation") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = defenseName,
                        onValueChange = { defenseName = it },
                        label = { Text("Active Defense Name (e.g. Intellectualization)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = defenseManifestation,
                        onValueChange = { defenseManifestation = it },
                        label = { Text("Patient Defense Manifestation Quote") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = culturalBarriers,
                        onValueChange = { culturalBarriers = it },
                        label = { Text("Cultural & Systemic Barriers") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = careAmbivalence,
                        onValueChange = { careAmbivalence = it },
                        label = { Text("Care-Seeking Ambivalence") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = openingStatement,
                        onValueChange = { openingStatement = it },
                        label = { Text("Patient Opening Statement") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val age = ageText.toIntOrNull() ?: 30
                            val customPersona = Persona(
                                id = "custom_" + System.currentTimeMillis(),
                                name = name.ifBlank { "Custom Patient" },
                                age = age,
                                occupation = occupation.ifBlank { "Client" },
                                primaryDiagnosis = diagnosis.ifBlank { "Adjustment Disorder" },
                                dsm5Code = dsmCode,
                                dsm5Formulation = formulation.ifBlank { "Client presents with emotional distress." },
                                activeDefenses = listOf(
                                    DefenseMechanism(
                                        name = defenseName.ifBlank { "Intellectualization" },
                                        definition = "Defensive detachment from painful emotions.",
                                        patientManifestation = defenseManifestation.ifBlank { "I just need a logical explanation." },
                                        counterStrategy = "Acknowledge cognitive awareness while grounding somatic affect."
                                    )
                                ),
                                culturalBarriers = culturalBarriers.ifBlank { "Stigma surrounding mental healthcare." },
                                careAmbivalence = careAmbivalence.ifBlank { "Hesitant regarding therapeutic efficacy." },
                                baselineDistress = DistressLevel.MODERATE,
                                initialMessage = openingStatement.ifBlank { "I'm not sure therapy can help me, but I'm willing to give it one session." },
                                isCustom = true
                            )
                            onSaveManual(customPersona)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotBlank() && diagnosis.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTealDark)
                    ) {
                        Text("Save & Launch Custom Vignette")
                    }
                }
            }
        }
    }
}
