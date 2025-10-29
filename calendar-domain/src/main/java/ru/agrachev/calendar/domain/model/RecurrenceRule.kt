package ru.agrachev.calendar.domain.model

enum class RecurrenceRule {
    NONE,
    PERIOD,
    WEEK,
    MONTH,
    YEAR;

    companion object {
        val DEFAULT = NONE
    }
}
