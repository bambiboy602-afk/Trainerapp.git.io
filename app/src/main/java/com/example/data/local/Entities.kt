package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DistressLevel
import com.example.data.model.MessageSender

@Entity(tableName = "chat_messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionId: Long,
    val personaId: String,
    val sender: String, // CLINICIAN, PATIENT, SUPERVISOR
    val text: String,
    val timestamp: Long,
    val distressLevel: String, // MILD, MODERATE, ELEVATED, SEVERE
    val activeDefense: String?,
    val supervisorTip: String?,
    val latencyMs: Long
)

@Entity(tableName = "clinical_sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val personaId: String,
    val startTime: Long,
    val durationSeconds: Long,
    val turnCount: Int,
    val lastUpdated: Long
)

@Entity(tableName = "custom_personas")
data class PersonaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val occupation: String,
    val primaryDiagnosis: String,
    val dsm5Code: String,
    val dsm5Formulation: String,
    val activeDefensesJson: String,
    val culturalBarriers: String,
    val careAmbivalence: String,
    val baselineDistress: String,
    val initialMessage: String,
    val speechPitch: Float,
    val speechRate: Float
)

@Entity(tableName = "supervisory_debriefs")
data class DebriefEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sessionId: Long,
    val personaId: String,
    val oarsOpenQuestions: Int,
    val oarsAffirmations: Int,
    val oarsReflections: Int,
    val oarsSummaries: Int,
    val stageOfChange: String,
    val resistanceTriggers: String, // Comma separated or JSON
    val rapportBuilders: String,
    val supervisorFeedback: String,
    val overallCompetencyScore: Int
)
