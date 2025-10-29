package ru.agrachev.calendar.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.agrachev.calendar.domain.model.CalendarEvent
import ru.agrachev.calendar.domain.model.CalendarRule

interface CalendarEventsRepository {

    fun getAllCalendarEvents(): Flow<List<CalendarRule>>

    suspend fun pushCalendarRule(calendarRule: CalendarRule)

    suspend fun cancelCalendarEvent(calendarEvent: CalendarEvent)

    suspend fun cancelCalendarRule(calendarRule: CalendarRule)
}
