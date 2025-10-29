package ru.agrachev.calendar.domain.mocks

import kotlinx.coroutines.Dispatchers
import ru.agrachev.calendar.domain.core.DateRange
import ru.agrachev.calendar.domain.model.CalendarEvent
import ru.agrachev.calendar.domain.model.CalendarRule
import ru.agrachev.calendar.domain.model.RecurrenceRule
import java.time.LocalDate
import java.time.YearMonth

val testDispatcher = Dispatchers.Default

val testDate: LocalDate = LocalDate.now()

val testDates: List<LocalDate> = YearMonth.of(testDate.year, testDate.month)
    .let {
        it.atDay(1)
            .datesUntil(it.plusMonths(1).atDay(1))
            .toList()
    }

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
