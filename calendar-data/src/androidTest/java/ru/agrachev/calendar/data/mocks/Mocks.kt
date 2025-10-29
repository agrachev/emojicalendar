package ru.agrachev.calendar.data.mocks

import ru.agrachev.calendar.domain.core.DateRange
import ru.agrachev.calendar.domain.model.CalendarEvent
import ru.agrachev.calendar.domain.model.CalendarRule
import ru.agrachev.calendar.domain.model.RecurrenceRule
import java.time.LocalDate

val testDate: LocalDate = LocalDate.now()

val testRule = CalendarRule(
    title = "Test",
    dateRange = DateRange.of(testDate, testDate.plusDays(1)),
    calendarEvents = emptyList(),
    recurrenceRule = RecurrenceRule.DEFAULT,
)

val testEvent = CalendarEvent(
    emoji = "😭",
    title = "Test",
    scheduledDate = testDate,
)

inline val newTestRule
    get() = testEvent.copy(rule = testRule).let {
        testRule.copy(
            calendarEvents = listOf(it)
        )
    }
