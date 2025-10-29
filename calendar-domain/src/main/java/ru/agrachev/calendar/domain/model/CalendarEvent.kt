package ru.agrachev.calendar.domain.model

import java.time.LocalDate

data class CalendarEvent(
    val id: Id = Id.Companion.UNIQUE,
    val title: String? = null,
    val emoji: String,
    val scheduledDate: LocalDate,
    val rule: CalendarRule? = null,
)
