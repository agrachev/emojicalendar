package ru.agrachev.presentation.scope.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import ru.agrachev.presentation.arch.EmojiCalendarIntent
import ru.agrachev.presentation.core.CalendarDateStorageKey
import ru.agrachev.presentation.model.MainCalendarDateUIModel
import ru.agrachev.presentation.viewmodel.CalendarMviStateHolder

internal class EmojiCalendarScope(
    private val viewModel: CalendarMviStateHolder,
) : MainCalendarScope {

    private var currentMonthIndexState
            by mutableIntStateOf(super.currentMonthIndex)

    override val currentMonthIndex
        get() = currentMonthIndexState

    override fun requestDayModelsCallback(monthOffset: CalendarDateStorageKey) =
        viewModel.accept(
            EmojiCalendarIntent.LoadDayModelsForMonth(
                monthOffsetFromNow = monthOffset,
            )
        )

    override fun openModalRequest(model: MainCalendarDateUIModel) = viewModel.accept(
        EmojiCalendarIntent.OpenEventsBrowserModal(
            model = model,
        )
    )

    override fun requestNumberOfWeeksCallback(monthOffset: Int) =
        viewModel.getNumberOfWeeks(monthOffset)

    override fun setCurrentMonthIndex(index: Int) {
        currentMonthIndexState = index
    }

}
