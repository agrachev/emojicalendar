package ru.agrachev.emojicalendar.presentation.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_DATE_INDEX
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_EMOJI
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_ID
import ru.agrachev.emojicalendar.presentation.model.CalendarEventUIModel.Defaults.DEFAULT_TITLE
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

operator fun IntRange.unaryMinus() = IntRange(
    start = -this.start,
    endInclusive = -this.endInclusive,
)

operator fun IntRange.plus(other: IntRange) = IntRange(
    start = this.start + other.start,
    endInclusive = this.endInclusive + other.endInclusive,
)

operator fun IntRange.minus(other: IntRange) = this + -other

operator fun IntRange.plus(value: Int) = IntRange(
    start = this.start + value,
    endInclusive = this.endInclusive + value,
)

operator fun IntRange.minus(value: Int) = this + -value

inline val <T : Number> T.dateItemIndex
    get() = this.toInt() + Constants.NOW_INDEX

inline val IntRange.dateItemIndexes
    get() = this + Constants.NOW_INDEX

inline val IntRange.regularOffsets
    get() = this - Constants.NOW_INDEX

inline val <T> T.length where T : ClosedRange<out Int>
    get() = this.endInclusive - this.start

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

inline val Float.fraction
    get() = this % 1f
