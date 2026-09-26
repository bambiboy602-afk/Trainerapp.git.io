package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.DistressLevel
import com.example.data.model.EmergingThemesReport
import com.example.data.model.Persona
import com.example.data.model.SupervisoryDebrief
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiClinicalService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generatePatientResponse(
        persona: Persona,
        history: List<ChatMessage>,
        clinicianInput: String
    ): PatientResponseResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank()) {
            return@withContext fallbackPatientResponse(persona, history, clinicianInput)
        }

        try {
            val systemPrompt = """
                You are role-playing as the mental health patient described below in a clinical counseling simulation training platform.
                PATIENT DOSSIER:
                - Name: ${persona.name}, Age: ${persona.age}, Occupation: ${persona.occupation}
                - Primary Diagnosis: ${persona.primaryDiagnosis} (${persona.dsm5Code})
                - DSM-5 Formulation: ${persona.dsm5Formulation}
                - Active Psychological Defenses: ${persona.activeDefenses.joinToString { "${it.name}: ${it.patientManifestation}" }}
                - Cultural & Systemic Barriers: ${persona.culturalBarriers}
                - Care Ambivalence: ${persona.careAmbivalence}
                - Current Baseline Distress: ${persona.baselineDistress.name}
                - Affective Markers: ${persona.affectiveMarkers.joinToString()}

                CRITICAL CLINICAL SIMULATION INSTRUCTIONS:
                1. Stay strictly in-character as ${persona.name}. Exhibit authentic care-seeking hesitancy, ambivalence, skepticism, or psychological defense.
                2. Do NOT be easily cured or compliant. Respond naturally to the counselor's phrasing. If the counselor gives unsolicited advice or confronts prematurely, trigger a defense or pull back. If the counselor uses an empathetic OARS reflection, open up slightly.
                3. You MUST respond with a VALID JSON OBJECT adhering strictly to this schema:
                {
                  "patientResponse": "In-character reply here (1 to 4 sentences)",
                  "distressLevel": "MILD" or "MODERATE" or "ELEVATED" or "SEVERE",
                  "activeDefense": "Name of defense activated, or None",
                  "supervisorCoachingTip": "A constructive 1-2 sentence coaching tip for the trainee clinician on how to apply Motivational Interviewing or OARS skills to this defense",
                  "simulatedLatencyMs": 900
                }
            """.trimIndent()

            val contentsArray = JSONArray()
            // Add prior turns
            val recentTurns = history.takeLast(6)
            for (msg in recentTurns) {
                val role = if (msg.sender == com.example.data.model.MessageSender.CLINICIAN) "user" else "model"
                contentsArray.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().apply { put("text", msg.text) }))
                })
            }
            // Add current input
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().apply { put("text", clinicianInput) }))
            })

            val requestBody = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply { put("text", systemPrompt) }))
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val rawBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                Log.w("GeminiClinicalService", "API error: ${response.code} $rawBody")
                return@withContext fallbackPatientResponse(persona, history, clinicianInput)
            }

            val root = JSONObject(rawBody)
            val candidateText = root.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: ""

            if (candidateText.isNotBlank()) {
                val parsed = JSONObject(candidateText)
                val text = parsed.optString("patientResponse", "")
                val distressStr = parsed.optString("distressLevel", persona.baselineDistress.name)
                val distress = try { DistressLevel.valueOf(distressStr) } catch (_: Exception) { persona.baselineDistress }
                val defense = parsed.optString("activeDefense", "None").takeIf { it != "None" }
                val tip = parsed.optString("supervisorCoachingTip", "")
                val latency = parsed.optLong("simulatedLatencyMs", 950L)

                return@withContext PatientResponseResult(
                    text = text,
                    distressLevel = distress,
                    activeDefense = defense,
                    supervisorTip = tip,
                    latencyMs = latency
                )
            }
            fallbackPatientResponse(persona, history, clinicianInput)
        } catch (e: Exception) {
            Log.e("GeminiClinicalService", "Error calling Gemini", e)
            fallbackPatientResponse(persona, history, clinicianInput)
        }
    }

    suspend fun analyzeEmergingThemes(
        persona: Persona,
        history: List<ChatMessage>
    ): EmergingThemesReport = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || history.isEmpty()) {
            return@withContext fallbackEmergingThemes(persona, history)
        }

        try {
            val transcript = history.joinToString("\n") { "${it.sender.name}: ${it.text}" }
            val prompt = """
                Analyze this clinical counseling transcript for patient ${persona.name} (${persona.primaryDiagnosis}).
                TRANSCRIPT:
                $transcript

                Extract psychological patterns as JSON:
                {
                  "themes": ["3 distinct psychological themes appearing in the discourse"],
                  "ambivalenceShifts": ["2 observations of movement toward change or withdrawal"],
                  "cognitiveTraps": ["2 cognitive distortions or recurring defense loops"],
                  "miNextSteps": ["2 concrete Motivational Interviewing recommendations for the clinician"]
                }
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply { put("text", prompt) }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val raw = response.body?.string() ?: ""
            val parsed = JSONObject(JSONObject(raw).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text"))

            val themes = parsed.getJSONArray("themes").let { (0 until it.length()).map { i -> it.getString(i) } }
            val ambivalence = parsed.getJSONArray("ambivalenceShifts").let { (0 until it.length()).map { i -> it.getString(i) } }
            val traps = parsed.getJSONArray("cognitiveTraps").let { (0 until it.length()).map { i -> it.getString(i) } }
            val nextSteps = parsed.getJSONArray("miNextSteps").let { (0 until it.length()).map { i -> it.getString(i) } }

            EmergingThemesReport(themes, ambivalence, traps, nextSteps)
        } catch (e: Exception) {
            Log.e("GeminiClinicalService", "Emerging themes error", e)
            fallbackEmergingThemes(persona, history)
        }
    }

    suspend fun generateSupervisoryDebrief(
        persona: Persona,
        history: List<ChatMessage>
    ): SupervisoryDebrief = withContext(Dispatchers.IO) {
        val clinicianTurns = history.filter { it.sender == com.example.data.model.MessageSender.CLINICIAN }
        var openQuestions = 0
        var affirmations = 0
        var reflections = 0
        var summaries = 0

        for (turn in clinicianTurns) {
            val lower = turn.text.lowercase().trim()
            if (lower.startsWith("how") || lower.startsWith("what") || lower.startsWith("tell me about") || lower.contains("what would happen")) {
                openQuestions++
            } else if (lower.contains("notice") || lower.contains("it sounds like") || lower.contains("you feel") || lower.contains("on one hand")) {
                reflections++
            } else if (lower.contains("appreciate") || lower.contains("courage") || lower.contains("strength") || lower.contains("handled")) {
                affirmations++
            } else if (lower.contains("so far") || lower.contains("to recap") || lower.contains("summariz")) {
                summaries++
            } else {
                reflections++
            }
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || history.size < 2) {
            return@withContext fallbackDebrief(persona, history, openQuestions, affirmations, reflections, summaries)
        }

        try {
            val transcript = history.joinToString("\n") { "${it.sender.name}: ${it.text}" }
            val prompt = """
                You are a senior clinical psychology supervisor evaluating a trainee's motivational interviewing session with patient ${persona.name} (${persona.primaryDiagnosis}).
                TRANSCRIPT:
                $transcript

                Evaluate the clinician according to OARS framework and Transtheoretical Model.
                Output strictly JSON:
                {
                  "stageOfChange": "Precontemplation" or "Contemplation" or "Preparation",
                  "resistanceTriggers": ["1 or 2 moments clinician increased resistance"],
                  "rapportBuilders": ["1 or 2 moments clinician demonstrated PACE empathy"],
                  "supervisorFeedback": "Paragraph with comprehensive clinical supervision feedback, highlighting strengths and growth opportunities in Motivational Interviewing",
                  "overallCompetencyScore": 88
                }
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply { put("text", prompt) }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val raw = response.body?.string() ?: ""
            val parsed = JSONObject(JSONObject(raw).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text"))

            val stage = parsed.optString("stageOfChange", "Contemplation")
            val triggers = parsed.optJSONArray("resistanceTriggers")?.let { (0 until it.length()).map { i -> it.getString(i) } } ?: listOf("Direct unsolicited advice")
            val builders = parsed.optJSONArray("rapportBuilders")?.let { (0 until it.length()).map { i -> it.getString(i) } } ?: listOf("Validation of autonomy")
            val feedback = parsed.optString("supervisorFeedback", "Good clinical pacing.")
            val score = parsed.optInt("overallCompetencyScore", 85)

            SupervisoryDebrief(
                sessionId = 0L,
                personaId = persona.id,
                oarsOpenQuestions = openQuestions,
                oarsAffirmations = affirmations,
                oarsReflections = reflections,
                oarsSummaries = summaries,
                stageOfChange = stage,
                resistanceTriggers = triggers,
                rapportBuilders = builders,
                supervisorFeedback = feedback,
                overallCompetencyScore = score
            )
        } catch (e: Exception) {
            Log.e("GeminiClinicalService", "Debrief error", e)
            fallbackDebrief(persona, history, openQuestions, affirmations, reflections, summaries)
        }
    }

    suspend fun generateSyntheticPersona(topicPrompt: String): Persona? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank()) {
            return@withContext createFallbackSyntheticPersona(topicPrompt)
        }

        try {
            val prompt = """
                Generate a rich clinical mental health training persona based on user request: "$topicPrompt".
                Adhere strictly to DSM-5-TR diagnostic criteria. Output valid JSON:
                {
                  "name": "Full Name",
                  "age": 34,
                  "occupation": "Job Title",
                  "primaryDiagnosis": "DSM-5 Diagnosis with ICD code",
                  "dsm5Code": "DSM-5-TR code",
                  "dsm5Formulation": "Clinical formulation of symptoms, etiology, and functional impairment",
                  "activeDefenses": [
                    {
                      "name": "Defense 1",
                      "definition": "Definition",
                      "patientManifestation": "Quote illustrating defense",
                      "counterStrategy": "Motivational interviewing strategy"
                    },
                    {
                      "name": "Defense 2",
                      "definition": "Definition",
                      "patientManifestation": "Quote",
                      "counterStrategy": "Strategy"
                    }
                  ],
                  "culturalBarriers": "Cultural / systemic barriers to care",
                  "careAmbivalence": "Nature of hesitancy to seek help",
                  "baselineDistress": "MODERATE",
                  "initialMessage": "Opening authentic statement from the patient arriving to session",
                  "speechPitch": 1.0,
                  "speechRate": 1.0,
                  "affectiveMarkers": ["Marker 1", "Marker 2", "Marker 3"]
                }
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().apply { put("text", prompt) }))
                }))
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val raw = response.body?.string() ?: ""
            val parsed = JSONObject(JSONObject(raw).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text"))

            val defensesJson = parsed.getJSONArray("activeDefenses")
            val defenses = (0 until defensesJson.length()).map { i ->
                val obj = defensesJson.getJSONObject(i)
                com.example.data.model.DefenseMechanism(
                    name = obj.getString("name"),
                    definition = obj.getString("definition"),
                    patientManifestation = obj.getString("patientManifestation"),
                    counterStrategy = obj.getString("counterStrategy")
                )
            }

            val markersJson = parsed.optJSONArray("affectiveMarkers")
            val markers = markersJson?.let { (0 until it.length()).map { i -> it.getString(i) } } ?: listOf("Affective Tension")

            val id = "synthetic_" + System.currentTimeMillis()
            Persona(
                id = id,
                name = parsed.getString("name"),
                age = parsed.getInt("age"),
                occupation = parsed.getString("occupation"),
                primaryDiagnosis = parsed.getString("primaryDiagnosis"),
                dsm5Code = parsed.getString("dsm5Code"),
                dsm5Formulation = parsed.getString("dsm5Formulation"),
                activeDefenses = defenses,
                culturalBarriers = parsed.getString("culturalBarriers"),
                careAmbivalence = parsed.getString("careAmbivalence"),
                baselineDistress = DistressLevel.valueOf(parsed.optString("baselineDistress", "MODERATE")),
                initialMessage = parsed.getString("initialMessage"),
                speechPitch = parsed.optDouble("speechPitch", 1.0).toFloat(),
                speechRate = parsed.optDouble("speechRate", 1.0).toFloat(),
                isCustom = true,
                affectiveMarkers = markers
            )
        } catch (e: Exception) {
            Log.e("GeminiClinicalService", "Synthetic persona error", e)
            createFallbackSyntheticPersona(topicPrompt)
        }
    }

    // High fidelity offline fallback dialogue simulation
    private fun fallbackPatientResponse(
        persona: Persona,
        history: List<ChatMessage>,
        clinicianInput: String
    ): PatientResponseResult {
        val input = clinicianInput.lowercase()
        val turnCount = history.size

        return when (persona.id) {
            "marcus_vance" -> {
                when {
                    input.contains("why") || input.contains("should") || input.contains("exercise") -> {
                        PatientResponseResult(
                            text = "You're telling me about sleep hygiene like I haven't tried sleeping. I'm a coach; I know how conditioning works. The problem is my body feels like it's dragging fifty pounds of wet concrete.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = "Somatization",
                            supervisorTip = "Notice how advice triggered somatic defensiveness. Shift to an open question exploring the weight of that fatigue.",
                            latencyMs = 1100L
                        )
                    }
                    input.contains("feel") || input.contains("heavy") || input.contains("exhausted") || input.contains("sound") -> {
                        PatientResponseResult(
                            text = "Yeah... heavy is the right word. Sometimes I sit in my truck in the school parking lot after practice and I just stare at the dashboard for twenty minutes before I can turn the key.",
                            distressLevel = DistressLevel.MODERATE,
                            activeDefense = null,
                            supervisorTip = "Rapport breakthrough. Use a simple reflection on what happens in those twenty minutes in the truck.",
                            latencyMs = 1400L
                        )
                    }
                    else -> {
                        PatientResponseResult(
                            text = "I just need to know if there's a fast way to get my energy back so I don't let these kids down. They're depending on me for regional championships.",
                            distressLevel = DistressLevel.MODERATE,
                            activeDefense = "Stoic Self-Reliance",
                            supervisorTip = "Affirm his deep dedication to his athletes, then develop discrepancy regarding his own sustainability.",
                            latencyMs = 900L
                        )
                    }
                }
            }
            "elena_rostova" -> {
                when {
                    input.contains("calm down") || input.contains("breathe") || input.contains("relax") -> {
                        PatientResponseResult(
                            text = "Telling me to 'just breathe' is physiologically patronizing. My sympathetic nervous system is responding to real architectural vulnerabilities. If the server drops, millions of dollars are lost.",
                            distressLevel = DistressLevel.SEVERE,
                            activeDefense = "Intellectualization",
                            supervisorTip = "Rolling with resistance: Never tell an anxious intellectualizer to 'calm down'. Validate the high stakes first.",
                            latencyMs = 800L
                        )
                    }
                    input.contains("pressure") || input.contains("standard") || input.contains("imposter") || input.contains("terrifying") -> {
                        PatientResponseResult(
                            text = "It feels like I'm walking on a frozen lake with thin ice. Everyone thinks I'm a rockstar architect, but inside I'm waiting for the crack to open.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = null,
                            supervisorTip = "Vulnerability surfaced. Acknowledge how exhausting it is to hold up that facade all day.",
                            latencyMs = 1200L
                        )
                    }
                    else -> {
                        PatientResponseResult(
                            text = "According to neuroscience literature, chronic panic can cause hippocampal volume reduction. So practically speaking, what concrete cognitive reframing tool do you recommend?",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = "Intellectualization",
                            supervisorTip = "Intellectual armor deployed. Acknowledge her clinical knowledge, then gently check in with her physical chest sensation.",
                            latencyMs = 950L
                        )
                    }
                }
            }
            "mateo_alvarez" -> {
                when {
                    input.contains("alcohol") || input.contains("drink") || input.contains("stop") -> {
                        PatientResponseResult(
                            text = "Look, I didn't come here to get lectured about beer. I work sixty hours on commercial roofs in 100-degree sun. If I want a few beers to shut my brain off, that's my business.",
                            distressLevel = DistressLevel.SEVERE,
                            activeDefense = "Denial & Avoidance",
                            supervisorTip = "Confrontational focus on substance triggered defensive anger. Reframe around his stated goal: getting deep sleep.",
                            latencyMs = 900L
                        )
                    }
                    input.contains("safe") || input.contains("confidential") || input.contains("respect") || input.contains("crew") -> {
                        PatientResponseResult(
                            text = "Alright. As long as we're clear that nobody touches my contracting license. Ever since that scaffold gave out under my buddy, loud noises make my skin jump right off my arms.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = "Hyper-Vigilant Startle Projection",
                            supervisorTip = "Safety established. Mateo acknowledged the scaffold trauma. Provide empathetic affirmation for his protective vigilance.",
                            latencyMs = 1300L
                        )
                    }
                    else -> {
                        PatientResponseResult(
                            text = "My wife says I snap at the kids over nothing. I don't mean to, but when there's shouting in the house, my heart slams like a nail gun.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = null,
                            supervisorTip = "Deep affective disclosure. Reflect the painful tension between loving his family and feeling physically on guard.",
                            latencyMs = 1100L
                        )
                    }
                }
            }
            "dr_arthur_pendelton" -> {
                when {
                    input.contains("stages of grief") || input.contains("move on") || input.contains("time heals") -> {
                        PatientResponseResult(
                            text = "'Time heals'? That is an insulting bromide invented by people too cowardly to contemplate the permanent mutilation of bereavement. Clara is gone; there is no 'healing' a missing limb.",
                            distressLevel = DistressLevel.SEVERE,
                            activeDefense = "Cynical Intellectual Armor",
                            supervisorTip = "Clinical cliche backfired. Arthur is philosophically acute. Apologize sincerely and honor his profound loyalty to Clara.",
                            latencyMs = 1200L
                        )
                    }
                    input.contains("clara") || input.contains("love") || input.contains("miss") || input.contains("forty-three") -> {
                        PatientResponseResult(
                            text = "We read poetry together every Sunday morning for forty-three years. Now Sunday mornings are... unbearable. The kitchen clock ticks like an execution sentence.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = null,
                            supervisorTip = "Sacred emotional space. Allow silence. Do not rush to fill this stillness with clinical tools.",
                            latencyMs = 1600L
                        )
                    }
                    else -> {
                        PatientResponseResult(
                            text = "I suppose you'll ask about my appetite or whether I've taken a walk. As if a walk around the neighborhood could reconcile forty-three years of companionship with an empty hallway.",
                            distressLevel = DistressLevel.MODERATE,
                            activeDefense = "Existential Apathy",
                            supervisorTip = "Existential barrier. Validate that walking cannot replace Clara, and explore what honoring her memory feels like.",
                            latencyMs = 1000L
                        )
                    }
                }
            }
            "priya_patel" -> {
                when {
                    input.contains("dirty") || input.contains("clean") || input.contains("safe") || input.contains("chair") -> {
                        PatientResponseResult(
                            text = "I know you think it's clean, but pathogens aren't visible. If I bring norovirus or staph back to my family apartment, my grandmother has chronic kidney disease. I couldn't live with myself.",
                            distressLevel = DistressLevel.SEVERE,
                            activeDefense = "Magical Undoing & Ritualization",
                            supervisorTip = "Reassurance trap. Do not debate microbial science. Reflect her profound love and protective fear for her grandmother.",
                            latencyMs = 950L
                        )
                    }
                    input.contains("exhausting") || input.contains("pressure") || input.contains("hands") || input.contains("knuckles") -> {
                        PatientResponseResult(
                            text = "My hands sting whenever I write with a pen because the skin splits open from the hot water. I missed my biochemistry recitation today because I was stuck washing in the library bathroom for 50 minutes. I feel like I'm losing my mind.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = null,
                            supervisorTip = "Severe distress with honest insight. Affirm her immense resilience under crushing academic and OCD pressure.",
                            latencyMs = 1350L
                        )
                    }
                    else -> {
                        PatientResponseResult(
                            text = "Can you just promise me that this room is thoroughly disinfected? If you confirm that, I can focus on talking.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = "Reassurance Seeking",
                            supervisorTip = "Withhold cognitive reassurance with compassion: 'Priya, OCD is demanding certainty that nobody can guarantee. Let's sit together with that worry.'",
                            latencyMs = 900L
                        )
                    }
                }
            }
            "jordan_taylor" -> {
                when {
                    input.contains("bipolar") || input.contains("manic") || input.contains("medication") || input.contains("lithium") -> {
                        PatientResponseResult(
                            text = "There it is. Right on schedule. You hear someone has boundless passion and creative energy, and your first clinical reflex is to slap a psychiatric label on it and drug me into a coma.",
                            distressLevel = DistressLevel.ELEVATED,
                            activeDefense = "Medical Mistrust & Invalidation Defense",
                            supervisorTip = "Defiance activated. Step out of the psychiatric authority role. Affirm his autonomy and creative mastery.",
                            latencyMs = 850L
                        )
                    }
                    input.contains("design") || input.contains("brilliant") || input.contains("crash") || input.contains("protect") -> {
                        PatientResponseResult(
                            text = "Look, the work is incredible, okay? But... deep down I know what comes next. In three weeks, the bottom drops out and I won't even be able to open my email. That part terrifies me.",
                            distressLevel = DistressLevel.MODERATE,
                            activeDefense = null,
                            supervisorTip = "Ambivalence unlocked! Jordan acknowledged the depressive crash. Explore how to protect his artistic future together.",
                            latencyMs = 1250L
                        )
                    }
                    else -> {
                        PatientResponseResult(
                            text = "I've produced five full campaign concepts since midnight. I don't need eight hours of sleep like average people. Why should I trade this electric feeling for grey mediocrity?",
                            distressLevel = DistressLevel.MODERATE,
                            activeDefense = "Euphoric Defiance & Romanticizing Mania",
                            supervisorTip = "Develop discrepancy: Explore how sleep deprivation historically affects the quality and delivery of his campaigns.",
                            latencyMs = 900L
                        )
                    }
                }
            }
            "tom_curb_mentor" -> {
                when {
                    input.contains("relapse") || input.contains("craving") || input.contains("using") || input.contains("drink") -> {
                        PatientResponseResult(
                            text = "You're looking at the bottle, but the bottle was just the relief valve. What was the setup? Where did the pressure build up two days ago when you went silent?",
                            distressLevel = DistressLevel.MILD,
                            activeDefense = "5 Boundary Failure Detection",
                            supervisorTip = "Tom's B.A.M.B.I. Step 2 (Awareness & The Domino Chain): Trace the trigger cascade before the behavioral explosion.",
                            latencyMs = 1100L
                        )
                    }
                    input.contains("stuck") || input.contains("hopeless") || input.contains("worthless") || input.contains("convict") -> {
                        PatientResponseResult(
                            text = "Failure is an event, brother. You've carried it on your shoulders so long it started wearing your name tag. Drop the bag. Put both feet flat on this Phoenix pavement.",
                            distressLevel = DistressLevel.MILD,
                            activeDefense = "The Lantern Boundary",
                            supervisorTip = "Identity boundary failure reflected. Tom separates the temporary action/record from core human dignity.",
                            latencyMs = 1200L
                        )
                    }
                    else -> {
                        PatientResponseResult(
                            text = "Sit with me on the curb for a minute. We're not solving next year right now. What does the next sixty minutes look like? What's the smallest useful move you can make today?",
                            distressLevel = DistressLevel.MILD,
                            activeDefense = "Street Truth Precision",
                            supervisorTip = "See -> Sit -> Move impulse protocol. Ground the nervous system before attempting trauma excavation.",
                            latencyMs = 950L
                        )
                    }
                }
            }
            else -> {
                PatientResponseResult(
                    text = "I hear what you're saying, but it's not that simple. I've been managing this on my own for a long time.",
                    distressLevel = persona.baselineDistress,
                    activeDefense = persona.activeDefenses.firstOrNull()?.name,
                    supervisorTip = "Use an open inquiry to understand their lived experience better.",
                    latencyMs = 950L
                )
            }
        }
    }

    private fun fallbackEmergingThemes(persona: Persona, history: List<ChatMessage>): EmergingThemesReport {
        return EmergingThemesReport(
            themes = listOf(
                "Tension between autonomy and vulnerability in ${persona.name}'s narrative",
                "Recurrent activation of ${persona.activeDefenses.firstOrNull()?.name ?: "psychological defenses"} under clinical inquiry",
                "Underlying distress masked by external occupational role requirements"
            ),
            ambivalenceShifts = listOf(
                "Client displays micro-openings when somatic or emotional reality is validated without advice",
                "Guarded withdrawal surfaces whenever institutional or medication themes are broached prematurely"
            ),
            cognitiveTraps = listOf(
                "All-or-nothing framing regarding strength vs needing clinical collaboration",
                "Catastrophic anticipation of professional invalidation or loss of control"
            ),
            miNextSteps = listOf(
                "Deploy complex double-sided reflections to honor the defensive armor while holding space for change",
                "Reframe clinical goal as client-directed endurance conditioning rather than symptom elimination"
            )
        )
    }

    private fun fallbackDebrief(
        persona: Persona,
        history: List<ChatMessage>,
        openQuestions: Int,
        affirmations: Int,
        reflections: Int,
        summaries: Int
    ): SupervisoryDebrief {
        return SupervisoryDebrief(
            sessionId = 0L,
            personaId = persona.id,
            oarsOpenQuestions = openQuestions.coerceAtLeast(3),
            oarsAffirmations = affirmations.coerceAtLeast(2),
            oarsReflections = reflections.coerceAtLeast(4),
            oarsSummaries = summaries.coerceAtLeast(1),
            stageOfChange = "Contemplation (Ambivalence Phase)",
            resistanceTriggers = listOf(
                "Premature problem-solving before validating ${persona.activeDefenses.firstOrNull()?.name ?: "defenses"}",
                "Direct confrontation on care-seeking barriers without sufficient autonomy support"
            ),
            rapportBuilders = listOf(
                "Empathetic pacing that acknowledged ${persona.name}'s occupational identity and personal dignity",
                "Non-defensive acceptance when patient tested boundaries or deployed intellectual armor"
            ),
            supervisorFeedback = "The clinician demonstrated solid therapeutic presence. Effective pacing allowed ${persona.name} to drop their guard. In upcoming sessions, prioritize developing discrepancy around the unsustainable daily cost of their defense mechanisms rather than challenging the defenses head-on.",
            overallCompetencyScore = 87
        )
    }

    private fun createFallbackSyntheticPersona(topicPrompt: String): Persona {
        val id = "synthetic_" + System.currentTimeMillis()
        return Persona(
            id = id,
            name = "Devon Sanders",
            age = 36,
            occupation = "Paramedic / First Responder",
            primaryDiagnosis = "Acute Stress Disorder / Burnout (F43.0)",
            dsm5Code = "DSM-5-TR: 308.3 (F43.0)",
            dsm5Formulation = "Secondary traumatic stress and chronic sympathetic overload from 12 years of emergency medical service, characterized by intrusive call memories, emotional detachment from spouse, and hyper-vigilant insomnia.",
            activeDefenses = listOf(
                com.example.data.model.DefenseMechanism(
                    name = "Humor & Deflection",
                    definition = "Using dark gallows humor to sidestep visceral horror.",
                    patientManifestation = "'If you don't laugh at the carnage, you lose your mind. We just tell jokes in the ambulance.'",
                    counterStrategy = "Acknowledge the survival utility of humor while holding gentle space for the silence behind it."
                ),
                com.example.data.model.DefenseMechanism(
                    name = "Compartmentalization",
                    definition = "Locking traumatic memories in a cognitive vault away from daily family life.",
                    patientManifestation = "'When I take off my uniform, I leave everything in the locker. Except lately the locker door won't shut.'",
                    counterStrategy = "Explore the emotional energy required to keep that locker door forced shut."
                )
            ),
            culturalBarriers = "Paramedic subculture stigma where asking for peer support is viewed as being unfit for emergency shifts.",
            careAmbivalence = "Agreed to consult only after experiencing a dissociative blank-out during a routine patient transfer.",
            baselineDistress = DistressLevel.ELEVATED,
            initialMessage = "I've worked 60-hour rotations for ten years without taking a mental health day. My supervisor said I looked 'distant' on last night's 4-alarm call. I'm fine, but department policy requires a sign-off.",
            speechPitch = 0.95f,
            speechRate = 1.05f,
            isCustom = true,
            affectiveMarkers = listOf("Gallows Humor", "Hyper-Arousal", "Emotional Detachment", "Pride")
        )
    }
}

data class PatientResponseResult(
    val text: String,
    val distressLevel: DistressLevel,
    val activeDefense: String?,
    val supervisorTip: String?,
    val latencyMs: Long
)
