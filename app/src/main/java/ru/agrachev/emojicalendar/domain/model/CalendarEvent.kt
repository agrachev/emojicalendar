package ru.agrachev.emojicalendar.domain.model

import java.time.LocalDate

data class CalendarEvent(
    val id: Id = Id.UNIQUE,
    val title: String? = null,
    val emoji: String,
    val scheduledDate: LocalDate,
    internal val rule: CalendarRule? = null,
)
