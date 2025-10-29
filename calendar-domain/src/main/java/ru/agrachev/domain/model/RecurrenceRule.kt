package ru.agrachev.calendardomain.model

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
