package ru.agrachev.calendar.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.agrachev.calendar.data.entity.CalendarEventEntity
import ru.agrachev.calendar.data.entity.CalendarRuleEntity
import ru.agrachev.calendar.data.entity.RuleWithEvents

@Dao
interface CalendarRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun pushCalendarRule(calendarRule: CalendarRuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun pushCalendarEvents(calendarEvents: List<CalendarEventEntity>)

    @Delete
    suspend fun deleteCalendarEvent(calendarEvent: CalendarEventEntity)

    @Delete
    suspend fun deleteCalendarRule(calendarRule: CalendarRuleEntity)

    @Transaction
    @Query("SELECT * FROM calendar_rules")
    fun getAllCalendarRules(): Flow<List<RuleWithEvents>>
}
