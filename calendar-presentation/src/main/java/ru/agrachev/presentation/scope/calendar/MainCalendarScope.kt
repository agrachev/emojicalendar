package ru.agrachev.calendarpresentation.scope.calendar

import ru.agrachev.calendarpresentation.core.CalendarDateStorageKey
import ru.agrachev.calendarpresentation.core.Constants.NOW_INDEX
import ru.agrachev.calendarpresentation.model.MainCalendarDateUIModel

internal interface MainCalendarScope {
    val currentMonthIndex: Int
        get() = NOW_INDEX

    fun requestDayModelsCallback(monthOffset: CalendarDateStorageKey)
    fun openModalRequest(model: MainCalendarDateUIModel)
    fun requestNumberOfWeeksCallback(monthOffset: Int): Int
    fun setCurrentMonthIndex(index: Int)
}
