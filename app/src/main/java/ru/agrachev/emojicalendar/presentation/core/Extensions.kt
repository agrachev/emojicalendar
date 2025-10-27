package ru.agrachev.emojicalendar.presentation.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import ru.agrachev.emojicalendar.domain.core.minus
import ru.agrachev.emojicalendar.domain.core.plus
import ru.agrachev.emojicalendar.presentation.core.Constants.NOW_INDEX
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_DATE_INDEX
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_EMOJI
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_ID
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_TITLE
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel

internal inline val <T : Number> T.dateItemIndex
    get() = this.toInt() + NOW_INDEX

internal inline val IntRange.dateItemIndexes
    get() = this + NOW_INDEX

internal inline val <T : Number> T.regularOffset
    get() = this.toInt() - NOW_INDEX

internal inline val IntRange.regularOffsets
    get() = this - NOW_INDEX

internal inline val CalendarEventUIModel?.nullableId
    get() = this?.id ?: DEFAULT_ID
internal inline val CalendarEventUIModel?.nullableTitle
    get() = this?.title ?: DEFAULT_TITLE
internal inline val CalendarEventUIModel?.nullableEmoji
    get() = this?.emoji ?: DEFAULT_EMOJI
internal inline val CalendarEventUIModel?.nullableDateIndex
    get() = this?.dateIndex ?: DEFAULT_DATE_INDEX

internal suspend inline fun <T> Flow<T>.observeStateChanges(collector: FlowCollector<T>) = this
    .drop(1)
    .distinctUntilChanged()
    .collect(collector)

internal typealias CalendarDateStorageKey = CacheKey<Int>
internal typealias CalendarDateStorageValue = List<MainCalendarDateUIModel>

internal inline val Int.value
    get() = this
