package ru.agrachev.calendar.domain.core

import org.threeten.extra.LocalDateRange
import java.io.Serializable
import java.time.LocalDate

class DateRange private constructor(
    private val dateRange: LocalDateRange,
) : Serializable {

    val start: LocalDate by dateRange::start
    val end: LocalDate by dateRange::end
    val endInclusive: LocalDate by dateRange::endInclusive

    fun lengthInDays() = dateRange.lengthInDays()

    companion object {
        fun of(startInclusive: LocalDate, endExclusive: LocalDate) =
            DateRange(
                LocalDateRange.of(startInclusive, endExclusive)
            )
    }
}
