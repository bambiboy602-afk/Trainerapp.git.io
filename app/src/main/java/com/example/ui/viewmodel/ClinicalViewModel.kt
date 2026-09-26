package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.CuratedCases
import com.example.data.model.DistressLevel
import com.example.data.model.EmergingThemesReport
import com.example.data.model.LiteratureItem
import com.example.data.model.MessageSender
import com.example.data.model.Persona
import com.example.data.model.SupervisoryDebrief
import com.example.data.repository.ClinicalRepository
import com.example.ui.tts.ClinicalTtsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ClinicalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ClinicalRepository(AppDatabase.getInstance(application).clinicalDao())
    val ttsManager = ClinicalTtsManager(application)

    val allPersonas: StateFlow<List<Persona>> = repository.getAllPersonas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CuratedCases.cases)

    private val _selectedPersona = MutableStateFlow<Persona?>(CuratedCases.cases.first())
    val selectedPersona: StateFlow<Persona?> = _selectedPersona.asStateFlow()

    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isPatientThinking = MutableStateFlow(false)
    val isPatientThinking: StateFlow<Boolean> = _isPatientThinking.asStateFlow()

    private val _turnLatency = MutableStateFlow(0L)
    val turnLatency: StateFlow<Long> = _turnLatency.asStateFlow()

    private val _currentDistress = MutableStateFlow(DistressLevel.MODERATE)
    val currentDistress: StateFlow<DistressLevel> = _currentDistress.asStateFlow()

    private val _activeDefense = MutableStateFlow<String?>("Somatization")
    val activeDefense: StateFlow<String?> = _activeDefense.asStateFlow()

    private val _supervisorTip = MutableStateFlow<String>("Acknowledge the physical symptom first; do not rush into psychological questioning.")
    val supervisorTip: StateFlow<String> = _supervisorTip.asStateFlow()

    private val _sessionDurationSeconds = MutableStateFlow(0L)
    val sessionDurationSeconds: StateFlow<Long> = _sessionDurationSeconds.asStateFlow()

    private val _isAnalyzingThemes = MutableStateFlow(false)
    val isAnalyzingThemes: StateFlow<Boolean> = _isAnalyzingThemes.asStateFlow()

    private val _emergingThemes = MutableStateFlow<EmergingThemesReport?>(null)
    val emergingThemes: StateFlow<EmergingThemesReport?> = _emergingThemes.asStateFlow()

    private val _debrief = MutableStateFlow<SupervisoryDebrief?>(null)
    val debrief: StateFlow<SupervisoryDebrief?> = _debrief.asStateFlow()

    private val _isGeneratingDebrief = MutableStateFlow(false)
    val isGeneratingDebrief: StateFlow<Boolean> = _isGeneratingDebrief.asStateFlow()

    private val _isGeneratingSyntheticPersona = MutableStateFlow(false)
    val isGeneratingSyntheticPersona: StateFlow<Boolean> = _isGeneratingSyntheticPersona.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val literatureList: StateFlow<List<LiteratureItem>> = combine(
        MutableStateFlow(CuratedCases.literatureList),
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.dsmCategory.contains(query, ignoreCase = true) ||
            it.keyFinding.contains(query, ignoreCase = true) ||
            it.clinicalApplication.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CuratedCases.literatureList)

    private var messageCollectionJob: Job? = null
    private var timerJob: Job? = null

    init {
        // Start first curated persona by default
        selectPersona(CuratedCases.cases.first())
        startSessionTimer()
    }

    private fun startSessionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _sessionDurationSeconds.value += 1
                _currentSessionId.value?.let { id ->
                    if (_sessionDurationSeconds.value % 10 == 0L) {
                        repository.updateSessionDuration(id, _sessionDurationSeconds.value)
                    }
                }
            }
        }
    }

    fun selectPersona(persona: Persona) {
        _selectedPersona.value = persona
        _currentDistress.value = persona.baselineDistress
        _activeDefense.value = persona.activeDefenses.firstOrNull()?.name
        _supervisorTip.value = "Initial encounter with ${persona.name}. Observe their verbal posture and resistance cues before choosing an inquiry."
        _sessionDurationSeconds.value = 0L

        viewModelScope.launch {
            val sessionId = repository.startOrResumeSession(persona)
            _currentSessionId.value = sessionId

            messageCollectionJob?.cancel()
            messageCollectionJob = launch {
                repository.getMessagesForSession(sessionId).collect { list ->
                    _messages.value = list
                    val lastPatientMsg = list.lastOrNull { it.sender == MessageSender.PATIENT }
                    if (lastPatientMsg != null) {
                        _currentDistress.value = lastPatientMsg.distressLevel
                        _activeDefense.value = lastPatientMsg.activeDefense
                        lastPatientMsg.supervisorTip?.let { _supervisorTip.value = it }
                    }
                }
            }
        }
    }

    fun sendMessage(text: String) {
        val persona = _selectedPersona.value ?: return
        val sessionId = _currentSessionId.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            _isPatientThinking.value = true
            val history = _messages.value
            try {
                val patientMsg = repository.sendClinicianMessage(sessionId, persona, history, text.trim())
                _currentDistress.value = patientMsg.distressLevel
                _activeDefense.value = patientMsg.activeDefense
                patientMsg.supervisorTip?.let { _supervisorTip.value = it }
                _turnLatency.value = patientMsg.latencyMs
            } finally {
                _isPatientThinking.value = false
            }
        }
    }

    fun resetSession() {
        val persona = _selectedPersona.value ?: return
        viewModelScope.launch {
            _sessionDurationSeconds.value = 0L
            val newSessionId = repository.resetSession(persona)
            _currentSessionId.value = newSessionId
            _emergingThemes.value = null
            _debrief.value = null
            selectPersona(persona)
        }
    }

    fun analyzeThemes() {
        val persona = _selectedPersona.value ?: return
        val history = _messages.value
        viewModelScope.launch {
            _isAnalyzingThemes.value = true
            try {
                val report = repository.analyzeThemes(persona, history)
                _emergingThemes.value = report
            } finally {
                _isAnalyzingThemes.value = false
            }
        }
    }

    fun generateDebrief() {
        val persona = _selectedPersona.value ?: return
        val sessionId = _currentSessionId.value ?: return
        val history = _messages.value
        viewModelScope.launch {
            _isGeneratingDebrief.value = true
            try {
                val report = repository.generateDebrief(sessionId, persona, history)
                _debrief.value = report
            } finally {
                _isGeneratingDebrief.value = false
            }
        }
    }

    fun speakMessage(message: ChatMessage) {
        val persona = _selectedPersona.value
        val pitch = persona?.speechPitch ?: 1.0f
        val rate = persona?.speechRate ?: 1.0f
        ttsManager.speak(message.text, pitch = pitch, speechRate = rate, utteranceId = message.id.toString())
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun saveCustomPersona(persona: Persona) {
        viewModelScope.launch {
            repository.saveCustomPersona(persona)
            selectPersona(persona)
        }
    }

    fun generateSyntheticPersona(prompt: String, onDone: (Persona) -> Unit) {
        viewModelScope.launch {
            _isGeneratingSyntheticPersona.value = true
            try {
                val generated = repository.generateSyntheticPersona(prompt)
                if (generated != null) {
                    repository.saveCustomPersona(generated)
                    selectPersona(generated)
                    onDone(generated)
                }
            } finally {
                _isGeneratingSyntheticPersona.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun generateSoapRecordText(): String {
        val persona = _selectedPersona.value ?: return "No active session."
        val msgs = _messages.value
        val durationMin = _sessionDurationSeconds.value / 60
        val durationSec = _sessionDurationSeconds.value % 60

        val transcript = msgs.joinToString("\n\n") {
            "[${it.sender.name}] (${it.distressLevel.name}): ${it.text}"
        }

        return """
            CLINICAL TRAINING ENCOUNTER RECORD (SOAP FORMAT)
            ================================================================
            PATIENT INFORMATION:
            Name: ${persona.name} | Age: ${persona.age} | Occupation: ${persona.occupation}
            DSM-5-TR Diagnosis: ${persona.primaryDiagnosis} (${persona.dsm5Code})
            Session Duration: ${durationMin}m ${durationSec}s | Total Turns: ${msgs.size}
            
            [S] SUBJECTIVE:
            Patient presented with initial baseline distress level: ${persona.baselineDistress.name}.
            Active psychological defenses manifested during encounter: ${persona.activeDefenses.joinToString { it.name }}.
            Care ambivalence noted: "${persona.careAmbivalence}".
            Cultural / systemic context: ${persona.culturalBarriers}.
            
            [O] OBJECTIVE:
            Affective markers displayed: ${persona.affectiveMarkers.joinToString()}.
            Affective distress trajectory ended at: ${_currentDistress.value.name}.
            Resistance events tracked: ${msgs.count { it.activeDefense != null }}.
            
            [A] ASSESSMENT:
            ${persona.dsm5Formulation}
            
            [P] PLAN & CLINICAL SUPERVISION RECOMMENDATIONS:
            Continue Motivational Interviewing pacing with PACE spirit (Partnership, Acceptance, Compassion, Evocation).
            Monitor and roll with active defense (${_activeDefense.value ?: "Defense"}).
            Supervisor Focus: ${_supervisorTip.value}
            
            ================================================================
            SESSION TRANSCRIPT:
            $transcript
            ================================================================
            Generated by ClinicalSim Platform for Academic Supervision & Training.
        """.trimIndent()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        ttsManager.shutdown()
    }
}
