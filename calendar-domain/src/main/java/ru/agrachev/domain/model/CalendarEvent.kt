package ru.agrachev.calendardomain.model

import java.time.LocalDate

data class CalendarEvent(
    val id: Id = Id.UNIQUE,
    val title: String? = null,
    val emoji: String,
    val scheduledDate: LocalDate,
    val rule: CalendarRule? = null,
)
