package ru.agrachev.emojicalendar.domain.model

import java.time.LocalDate

data class CalendarDate(
    val date: LocalDate,
    val scheduledEvents: List<CalendarEvent> = emptyList(),
)
