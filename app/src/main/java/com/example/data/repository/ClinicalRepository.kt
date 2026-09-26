package com.example.data.repository

import com.example.data.local.ClinicalDao
import com.example.data.local.DebriefEntity
import com.example.data.local.MessageEntity
import com.example.data.local.PersonaEntity
import com.example.data.local.SessionEntity
import com.example.data.model.ChatMessage
import com.example.data.model.ClinicalSession
import com.example.data.model.CuratedCases
import com.example.data.model.DefenseMechanism
import com.example.data.model.DistressLevel
import com.example.data.model.EmergingThemesReport
import com.example.data.model.LiteratureItem
import com.example.data.model.MessageSender
import com.example.data.model.Persona
import com.example.data.model.SupervisoryDebrief
import com.example.data.remote.GeminiClinicalService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ClinicalRepository(
    private val dao: ClinicalDao,
    private val geminiService: GeminiClinicalService = GeminiClinicalService()
) {
    // Curated + Custom personas
    fun getAllPersonas(): Flow<List<Persona>> {
        return dao.getAllCustomPersonas().map { customEntities ->
            val customPersonas = customEntities.map { entity ->
                val defenses = try {
                    val arr = JSONArray(entity.activeDefensesJson)
                    (0 until arr.length()).map { i ->
                        val obj = arr.getJSONObject(i)
                        DefenseMechanism(
                            name = obj.getString("name"),
                            definition = obj.getString("definition"),
                            patientManifestation = obj.getString("patientManifestation"),
                            counterStrategy = obj.getString("counterStrategy")
                        )
                    }
                } catch (_: Exception) {
                    emptyList()
                }

                Persona(
                    id = entity.id,
                    name = entity.name,
                    age = entity.age,
                    occupation = entity.occupation,
                    primaryDiagnosis = entity.primaryDiagnosis,
                    dsm5Code = entity.dsm5Code,
                    dsm5Formulation = entity.dsm5Formulation,
                    activeDefenses = defenses,
                    culturalBarriers = entity.culturalBarriers,
                    careAmbivalence = entity.careAmbivalence,
                    baselineDistress = try { DistressLevel.valueOf(entity.baselineDistress) } catch (_: Exception) { DistressLevel.MODERATE },
                    initialMessage = entity.initialMessage,
                    speechPitch = entity.speechPitch,
                    speechRate = entity.speechRate,
                    isCustom = true
                )
            }
            CuratedCases.cases + customPersonas
        }
    }

    suspend fun getPersonaById(id: String): Persona? {
        val curated = CuratedCases.cases.find { it.id == id }
        if (curated != null) return curated

        val customList = CuratedCases.cases // fallback search
        return curated
    }

    // Sessions & Messages
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessage>> {
        return dao.getMessagesForSession(sessionId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    sessionId = entity.sessionId,
                    personaId = entity.personaId,
                    sender = try { MessageSender.valueOf(entity.sender) } catch (_: Exception) { MessageSender.PATIENT },
                    text = entity.text,
                    timestamp = entity.timestamp,
                    distressLevel = try { DistressLevel.valueOf(entity.distressLevel) } catch (_: Exception) { DistressLevel.MODERATE },
                    activeDefense = entity.activeDefense,
                    supervisorTip = entity.supervisorTip,
                    latencyMs = entity.latencyMs
                )
            }
        }
    }

    suspend fun startOrResumeSession(persona: Persona): Long {
        val existing = dao.getLatestSessionForPersona(persona.id)
        if (existing != null) {
            return existing.id
        }

        val newSessionId = dao.insertSession(
            SessionEntity(
                personaId = persona.id,
                startTime = System.currentTimeMillis(),
                durationSeconds = 0L,
                turnCount = 1,
                lastUpdated = System.currentTimeMillis()
            )
        )

        // Seed initial patient opening message
        dao.insertMessage(
            MessageEntity(
                sessionId = newSessionId,
                personaId = persona.id,
                sender = MessageSender.PATIENT.name,
                text = persona.initialMessage,
                timestamp = System.currentTimeMillis(),
                distressLevel = persona.baselineDistress.name,
                activeDefense = persona.activeDefenses.firstOrNull()?.name,
                supervisorTip = "Initial presentation: Note patient's baseline defense and care ambivalence. Use an open inquiry or affirming reflection to build psychological safety.",
                latencyMs = 500L
            )
        )

        return newSessionId
    }

    suspend fun resetSession(persona: Persona): Long {
        val newSessionId = dao.insertSession(
            SessionEntity(
                personaId = persona.id,
                startTime = System.currentTimeMillis(),
                durationSeconds = 0L,
                turnCount = 1,
                lastUpdated = System.currentTimeMillis()
            )
        )

        dao.insertMessage(
            MessageEntity(
                sessionId = newSessionId,
                personaId = persona.id,
                sender = MessageSender.PATIENT.name,
                text = persona.initialMessage,
                timestamp = System.currentTimeMillis(),
                distressLevel = persona.baselineDistress.name,
                activeDefense = persona.activeDefenses.firstOrNull()?.name,
                supervisorTip = "Fresh session started: Observe the patient's initial affective state and hesitation before responding.",
                latencyMs = 500L
            )
        )

        return newSessionId
    }

    suspend fun sendClinicianMessage(
        sessionId: Long,
        persona: Persona,
        history: List<ChatMessage>,
        text: String
    ): ChatMessage {
        // Insert clinician message
        val clinicianMsgId = dao.insertMessage(
            MessageEntity(
                sessionId = sessionId,
                personaId = persona.id,
                sender = MessageSender.CLINICIAN.name,
                text = text,
                timestamp = System.currentTimeMillis(),
                distressLevel = history.lastOrNull { it.sender == MessageSender.PATIENT }?.distressLevel?.name ?: persona.baselineDistress.name,
                activeDefense = null,
                supervisorTip = null,
                latencyMs = 0L
            )
        )

        // Update session turns
        val session = dao.getSessionById(sessionId)
        if (session != null) {
            dao.updateSession(session.copy(turnCount = session.turnCount + 1, lastUpdated = System.currentTimeMillis()))
        }

        // Generate patient reaction
        val patientResult = geminiService.generatePatientResponse(persona, history, text)

        val patientMsgId = dao.insertMessage(
            MessageEntity(
                sessionId = sessionId,
                personaId = persona.id,
                sender = MessageSender.PATIENT.name,
                text = patientResult.text,
                timestamp = System.currentTimeMillis(),
                distressLevel = patientResult.distressLevel.name,
                activeDefense = patientResult.activeDefense,
                supervisorTip = patientResult.supervisorTip,
                latencyMs = patientResult.latencyMs
            )
        )

        return ChatMessage(
            id = patientMsgId,
            sessionId = sessionId,
            personaId = persona.id,
            sender = MessageSender.PATIENT,
            text = patientResult.text,
            timestamp = System.currentTimeMillis(),
            distressLevel = patientResult.distressLevel,
            activeDefense = patientResult.activeDefense,
            supervisorTip = patientResult.supervisorTip,
            latencyMs = patientResult.latencyMs
        )
    }

    suspend fun updateSessionDuration(sessionId: Long, durationSeconds: Long) {
        val session = dao.getSessionById(sessionId)
        if (session != null) {
            dao.updateSession(session.copy(durationSeconds = durationSeconds, lastUpdated = System.currentTimeMillis()))
        }
    }

    suspend fun analyzeThemes(persona: Persona, history: List<ChatMessage>): EmergingThemesReport {
        return geminiService.analyzeEmergingThemes(persona, history)
    }

    suspend fun generateDebrief(sessionId: Long, persona: Persona, history: List<ChatMessage>): SupervisoryDebrief {
        val debrief = geminiService.generateSupervisoryDebrief(persona, history)
        dao.insertDebrief(
            DebriefEntity(
                sessionId = sessionId,
                personaId = persona.id,
                oarsOpenQuestions = debrief.oarsOpenQuestions,
                oarsAffirmations = debrief.oarsAffirmations,
                oarsReflections = debrief.oarsReflections,
                oarsSummaries = debrief.oarsSummaries,
                stageOfChange = debrief.stageOfChange,
                resistanceTriggers = debrief.resistanceTriggers.joinToString("||"),
                rapportBuilders = debrief.rapportBuilders.joinToString("||"),
                supervisorFeedback = debrief.supervisorFeedback,
                overallCompetencyScore = debrief.overallCompetencyScore
            )
        )
        return debrief
    }

    suspend fun saveCustomPersona(persona: Persona) {
        val defensesArr = JSONArray()
        persona.activeDefenses.forEach { def ->
            defensesArr.put(JSONObject().apply {
                put("name", def.name)
                put("definition", def.definition)
                put("patientManifestation", def.patientManifestation)
                put("counterStrategy", def.counterStrategy)
            })
        }

        dao.insertCustomPersona(
            PersonaEntity(
                id = persona.id,
                name = persona.name,
                age = persona.age,
                occupation = persona.occupation,
                primaryDiagnosis = persona.primaryDiagnosis,
                dsm5Code = persona.dsm5Code,
                dsm5Formulation = persona.dsm5Formulation,
                activeDefensesJson = defensesArr.toString(),
                culturalBarriers = persona.culturalBarriers,
                careAmbivalence = persona.careAmbivalence,
                baselineDistress = persona.baselineDistress.name,
                initialMessage = persona.initialMessage,
                speechPitch = persona.speechPitch,
                speechRate = persona.speechRate
            )
        )
    }

    suspend fun generateSyntheticPersona(prompt: String): Persona? {
        return geminiService.generateSyntheticPersona(prompt)
    }

    fun getLiteratureList(): List<LiteratureItem> = CuratedCases.literatureList
}
