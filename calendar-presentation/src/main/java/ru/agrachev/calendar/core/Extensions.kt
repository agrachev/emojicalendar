package ru.agrachev.presentation.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import ru.agrachev.calendardomain.core.minus
import ru.agrachev.calendardomain.core.plus
import ru.agrachev.presentation.model.CalendarEventUIModel
import ru.agrachev.presentation.model.MainCalendarDateUIModel

internal inline val <T : Number> T.dateItemIndex
    get() = this.toInt() + Constants.NOW_INDEX

internal inline val IntRange.dateItemIndexes
    get() = this + Constants.NOW_INDEX

internal inline val <T : Number> T.regularOffset
    get() = this.toInt() - Constants.NOW_INDEX

internal inline val IntRange.regularOffsets
    get() = this - Constants.NOW_INDEX

internal inline val CalendarEventUIModel?.nullableId
    get() = this?.id ?: CalendarEventUIModel.Defaults.DEFAULT_ID
internal inline val CalendarEventUIModel?.nullableTitle
    get() = this?.title ?: CalendarEventUIModel.Defaults.DEFAULT_TITLE
internal inline val CalendarEventUIModel?.nullableEmoji
    get() = this?.emoji ?: CalendarEventUIModel.Defaults.DEFAULT_EMOJI
internal inline val CalendarEventUIModel?.nullableDateIndex
    get() = this?.dateIndex ?: CalendarEventUIModel.Defaults.DEFAULT_DATE_INDEX

internal suspend inline fun <T> Flow<T>.observeStateChanges(collector: FlowCollector<T>) = this
    .drop(1)
    .distinctUntilChanged()
    .collect(collector)

internal typealias CalendarDateStorageKey = CacheKey<Int>
internal typealias CalendarDateStorageValue = List<MainCalendarDateUIModel>

internal inline val Int.value
    get() = this
