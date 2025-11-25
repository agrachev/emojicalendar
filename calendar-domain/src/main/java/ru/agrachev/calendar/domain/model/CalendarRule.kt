package ru.agrachev.calendar.domain.model

import ru.agrachev.calendar.domain.core.Constants.WEEK_DAY_COUNT
import ru.agrachev.calendar.domain.core.DateRange
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

data class CalendarRule(
    val id: Id = Id.UNIQUE,
    val title: String,
    val dateRange: DateRange,
    val calendarEvents: List<CalendarEvent>,
    val recurrenceRule: RecurrenceRule,
) {

    fun getMatchingEvent(targetDate: LocalDate): CalendarEvent? =
        when (recurrenceRule) {
            RecurrenceRule.NONE -> calendarEvents.firstOrNull {
                it.scheduledDate.isEqual(targetDate)
            }

            RecurrenceRule.PERIOD -> {
                dateRange.lengthInDays().let { rangeLength ->
                    calendarEvents.firstOrNull {
                        ChronoUnit.DAYS.between(
                            it.scheduledDate,
                            targetDate
                        ) % rangeLength == 0L
                    }
                }
            }

            RecurrenceRule.WEEK -> calendarEvents.firstOrNull {
                ChronoUnit.DAYS.between(it.scheduledDate, targetDate) % WEEK_DAY_COUNT == 0L
            }

            RecurrenceRule.MONTH -> TODO()
            RecurrenceRule.YEAR -> TODO()
        }
            .takeIf { event ->
                event?.let {
                    it.scheduledDate.until(targetDate, ChronoUnit.DAYS) >= 0
                } == true
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
