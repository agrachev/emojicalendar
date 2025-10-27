package ru.agrachev.emojicalendar.domain.model

import org.threeten.extra.LocalDateRange
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

data class CalendarRule(
    val id: Id = Id.UNIQUE,
    val title: String,
    val dateRange: LocalDateRange,
    val calendarEvents: List<CalendarEvent>,
    val recurrenceRule: RecurrenceRule,
) {

    fun getMatchingEvent(targetDate: LocalDate): CalendarEvent? =
        when (recurrenceRule) {
            RecurrenceRule.NONE -> calendarEvents.firstOrNull {
                it.scheduledDate == targetDate
            }

            RecurrenceRule.PERIOD -> {
                dateRange.lengthInDays().let { rangeLength ->
                    calendarEvents.firstOrNull {
                        ChronoUnit.DAYS.between(it.scheduledDate, targetDate) % rangeLength == 0L
                    }
                }
            }

            RecurrenceRule.WEEK -> calendarEvents.firstOrNull {
                ChronoUnit.DAYS.between(it.scheduledDate, targetDate) % 7 == 0L
            }

            RecurrenceRule.MONTH -> TODO()
            RecurrenceRule.YEAR -> TODO()
        }
}

@JvmInline
value class Id(
    val value: UUID
) {
    companion object {
        val UNIQUE
            get() = Id(UUID.randomUUID())
    }
}
