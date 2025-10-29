package ru.agrachev.calendardomain.repository

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

interface CalendarDataRepository {

    fun getLocalizedWeekdayNames(
        locale: Locale = Locale.getDefault(),
        style: TextStyle = TextStyle.FULL,
    ): List<String>

    fun getLocalizedMonthNames(
        locale: Locale = Locale.getDefault(),
        style: TextStyle = TextStyle.FULL,
    ): List<String>

    fun requestDates(
        monthOffset: Int,
        originDate: LocalDate = LocalDate.now(),
        locale: Locale = Locale.getDefault(),
    ): List<LocalDate>

    fun getNumberOfWeeks(
        monthOffset: Int,
        originDate: LocalDate = LocalDate.now(),
        locale: Locale = Locale.getDefault(),
    ): Int
}
