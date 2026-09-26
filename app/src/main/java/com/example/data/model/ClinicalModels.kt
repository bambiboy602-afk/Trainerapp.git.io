package com.example.data.model

enum class DistressLevel(val label: String, val score: Int) {
    MILD("Mild", 25),
    MODERATE("Moderate", 50),
    ELEVATED("Elevated", 75),
    SEVERE("Severe", 95)
}

enum class MessageSender {
    CLINICIAN,
    PATIENT,
    SUPERVISOR
}

data class DefenseMechanism(
    val name: String,
    val definition: String,
    val patientManifestation: String,
    val counterStrategy: String
)

data class Persona(
    val id: String,
    val name: String,
    val age: Int,
    val occupation: String,
    val primaryDiagnosis: String,
    val dsm5Code: String,
    val dsm5Formulation: String,
    val activeDefenses: List<DefenseMechanism>,
    val culturalBarriers: String,
    val careAmbivalence: String,
    val baselineDistress: DistressLevel,
    val initialMessage: String,
    val speechPitch: Float = 1.0f,
    val speechRate: Float = 0.95f,
    val isCustom: Boolean = false,
    val affectiveMarkers: List<String> = emptyList()
)

data class ChatMessage(
    val id: Long = 0L,
    val sessionId: Long,
    val personaId: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val distressLevel: DistressLevel = DistressLevel.MODERATE,
    val activeDefense: String? = null,
    val supervisorTip: String? = null,
    val latencyMs: Long = 800L
)

data class ClinicalSession(
    val id: Long = 0L,
    val personaId: String,
    val startTime: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0L,
    val turnCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class SupervisoryDebrief(
    val id: Long = 0L,
    val sessionId: Long,
    val personaId: String,
    val oarsOpenQuestions: Int = 0,
    val oarsAffirmations: Int = 0,
    val oarsReflections: Int = 0,
    val oarsSummaries: Int = 0,
    val stageOfChange: String = "Contemplation",
    val resistanceTriggers: List<String> = emptyList(),
    val rapportBuilders: List<String> = emptyList(),
    val supervisorFeedback: String = "",
    val overallCompetencyScore: Int = 85
)

data class EmergingThemesReport(
    val themes: List<String>,
    val ambivalenceShifts: List<String>,
    val cognitiveTraps: List<String>,
    val miNextSteps: List<String>
)

data class LiteratureItem(
    val id: String,
    val title: String,
    val journal: String,
    val year: String,
    val dsmCategory: String,
    val keyFinding: String,
    val clinicalApplication: String,
    val evidenceGrade: String
)
