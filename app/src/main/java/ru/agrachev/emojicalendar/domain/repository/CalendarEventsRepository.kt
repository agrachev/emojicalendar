package ru.agrachev.emojicalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.agrachev.emojicalendar.domain.model.CalendarEvent
import ru.agrachev.emojicalendar.domain.model.CalendarRule

interface CalendarEventsRepository {

    fun getAllCalendarEvents(): Flow<List<CalendarRule>>

    suspend fun pushCalendarRule(calendarRule: CalendarRule)

    suspend fun cancelCalendarEvent(calendarEvent: CalendarEvent)

    suspend fun cancelCalendarRule(calendarRule: CalendarRule)
}
