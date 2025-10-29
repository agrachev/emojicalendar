package ru.agrachev.calendar.data.mocks

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val testDate: LocalDate = LocalDate.now()

val availableLocales = Locale.getAvailableLocales()
    .filterNotNull()

val testLocales = listOf("us", "ru")
    .mapNotNull {
        Locale.forLanguageTag(it)
    }.toTypedArray()

inline val isoTestDate: String
    get() = DateTimeFormatter.ISO_DATE_TIME.format(testDate)
