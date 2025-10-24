package ru.agrachev.emojicalendar.data.repository

import ru.agrachev.emojicalendar.domain.repository.CalendarDataRepository
import ru.agrachev.emojicalendar.presentation.widget.toInt
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class EmojiCalendarDataRepository : CalendarDataRepository {

    override fun getLocalizedWeekdayNames(locale: Locale, style: TextStyle) =
        with(WeekFields.of(locale).firstDayOfWeek) {
            (0L..<7L)
                .map { dayOfWeekIndex ->
                    plus(dayOfWeekIndex)
                        .getDisplayName(style, locale)
                }
        }

    override fun getLocalizedMonthNames(locale: Locale, style: TextStyle) =
        Month.entries.map {
            it.getDisplayName(style, locale)
        }

    override fun requestDates(
        monthOffset: Int,
        originDate: LocalDate,
        locale: Locale,
    ): List<LocalDate> {
        val offsetMonth = originDate.getYearMonthWithMonthOffset(monthOffset)
        val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
        val firstMonday = originDate
            .plusMonths(monthOffset.toLong())
            .withDayOfMonth(1)
            .with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        val lastSunday = offsetMonth
            .atEndOfMonth()
            .with(TemporalAdjusters.previousOrSame(firstDayOfWeek.plus(6L)))
        return (0L..ChronoUnit.DAYS.between(firstMonday, lastSunday))
            .map { offset ->
                firstMonday.plusDays(offset)
            }
    }

    override fun getNumberOfWeeks(
        monthOffset: Int,
        originDate: LocalDate,
        locale: Locale,
    ): Int {
        val offsetMonth = originDate.getYearMonthWithMonthOffset(monthOffset)
        val weekFields = WeekFields.of(locale)
        val weekOfMonth = weekFields.weekOfMonth()
        val lastDayOfMonth = offsetMonth.atEndOfMonth()
        val lastWeekNumber = lastDayOfMonth.get(weekOfMonth)
        val firstWeekNumber = offsetMonth.atDay(1).get(weekOfMonth)
        val isLastDayOfMonthLastDayOfWeek =
            lastDayOfMonth.dayOfWeek == weekFields.firstDayOfWeek - 1
        return lastWeekNumber - firstWeekNumber + isLastDayOfMonthLastDayOfWeek.toInt()
    }
}

private fun LocalDate.getYearMonthWithMonthOffset(monthOffset: Int) =
    YearMonth.of(this.year, this.month)
        .plusMonths(monthOffset.toLong())
