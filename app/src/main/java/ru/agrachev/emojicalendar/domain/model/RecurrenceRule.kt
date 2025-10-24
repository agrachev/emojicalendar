package ru.agrachev.emojicalendar.domain.model

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
