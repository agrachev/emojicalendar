package ru.agrachev.calendarpresentation.core

import ru.agrachev.calendarpresentation.model.CalendarEventUIModel

internal object Constants {
    const val INFINITE = Int.MAX_VALUE
    const val NOW_INDEX = INFINITE / 2

    val CALENDAR_EVENTS_UI_MODEL_COMPARATOR = Comparator<CalendarEventUIModel> { o1, o2 ->
        o1.dateIndex.compareTo(o2.dateIndex)
    }
}
