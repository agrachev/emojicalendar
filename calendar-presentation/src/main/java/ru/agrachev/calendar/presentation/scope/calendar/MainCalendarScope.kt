package ru.agrachev.calendar.presentation.scope.calendar

import ru.agrachev.calendar.presentation.core.CalendarDateStorageKey
import ru.agrachev.calendar.presentation.core.Constants.NOW_INDEX
import ru.agrachev.calendar.presentation.model.MainCalendarDateUIModel

internal interface MainCalendarScope {
    val currentMonthIndex: Int
        get() = NOW_INDEX

    fun requestDayModelsCallback(monthOffset: CalendarDateStorageKey)
    fun openModalRequest(model: MainCalendarDateUIModel)
    fun requestNumberOfWeeksCallback(monthOffset: Int): Int
    fun setCurrentMonthIndex(index: Int)
}
