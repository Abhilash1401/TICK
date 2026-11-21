package com.example.widget.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.widget.data.model.CountdownEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CountdownDao {
    @Query("SELECT * FROM countdown_events ORDER BY isPinned DESC, targetTimestampMillis ASC")
    fun getAllEvents(): Flow<List<CountdownEvent>>

    @Query("SELECT * FROM countdown_events WHERE id = :id")
    fun getEventById(id: Long): Flow<CountdownEvent?>

    @Query("SELECT * FROM countdown_events WHERE id = :id")
    suspend fun getEventByIdDirect(id: Long): CountdownEvent?

    @Query("SELECT * FROM countdown_events WHERE isPinned = 1 LIMIT 1")
    fun getPinnedEvent(): Flow<CountdownEvent?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CountdownEvent): Long

    @Update
    suspend fun updateEvent(event: CountdownEvent)

    @Delete
    suspend fun deleteEvent(event: CountdownEvent)

    @Query("DELETE FROM countdown_events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("UPDATE countdown_events SET isPinned = CASE WHEN id = :eventId THEN 1 ELSE 0 END")
    suspend fun setPinnedEvent(eventId: Long)

    @Query("SELECT COUNT(*) FROM countdown_events")
    suspend fun getEventCount(): Int
}
