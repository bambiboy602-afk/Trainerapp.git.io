package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicalDao {
    // Messages
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun clearMessagesForSession(sessionId: Long)

    // Sessions
    @Query("SELECT * FROM clinical_sessions ORDER BY lastUpdated DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM clinical_sessions WHERE personaId = :personaId ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestSessionForPersona(personaId: String): SessionEntity?

    @Query("SELECT * FROM clinical_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): SessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Update
    suspend fun updateSession(session: SessionEntity)

    // Custom Personas
    @Query("SELECT * FROM custom_personas")
    fun getAllCustomPersonas(): Flow<List<PersonaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomPersona(persona: PersonaEntity)

    @Query("DELETE FROM custom_personas WHERE id = :id")
    suspend fun deleteCustomPersona(id: String)

    // Debriefs
    @Query("SELECT * FROM supervisory_debriefs WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getDebriefForSession(sessionId: Long): DebriefEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebrief(debrief: DebriefEntity): Long
}
