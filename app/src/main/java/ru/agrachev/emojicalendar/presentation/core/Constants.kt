package ru.agrachev.emojicalendar.presentation.core

import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel

object Constants {

    const val NOW_INDEX = Int.MAX_VALUE / 2

    val CALENDAR_EVENTS_UI_MODEL_COMPARATOR = Comparator<CalendarEventUIModel> { o1, o2 ->
        o1.dateIndex.compareTo(o2.dateIndex)
    }
}
