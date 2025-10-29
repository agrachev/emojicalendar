package ru.agrachev.calendardomain.repository

import kotlinx.coroutines.flow.Flow
import ru.agrachev.calendardomain.model.CalendarEvent
import ru.agrachev.calendardomain.model.CalendarRule

interface CalendarEventsRepository {

    fun getAllCalendarEvents(): Flow<List<CalendarRule>>

    suspend fun pushCalendarRule(calendarRule: CalendarRule)

    suspend fun cancelCalendarEvent(calendarEvent: CalendarEvent)

    suspend fun cancelCalendarRule(calendarRule: CalendarRule)
}
