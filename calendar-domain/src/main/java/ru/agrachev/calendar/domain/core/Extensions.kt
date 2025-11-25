package ru.agrachev.calendar.domain.core

import kotlin.math.abs

fun Boolean.toInt() = this.compareTo(false)
fun Boolean.toFloat() = this.toInt().toFloat()

operator fun IntRange.unaryMinus() = IntRange(
    start = -this.first,
    endInclusive = -this.last,
)

operator fun IntRange.plus(other: IntRange) = IntRange(
    start = this.first + other.first,
    endInclusive = this.last + other.last,
)

operator fun IntRange.minus(other: IntRange) = this + -other

operator fun IntRange.plus(value: Int) = IntRange(
    start = this.first + value,
    endInclusive = this.last + value,
)

operator fun IntRange.minus(value: Int) = this + -value

inline val <T> T.length where T : ClosedRange<out Int>
    get() = abs(this.endInclusive - this.start)

inline val Float.fraction
    get() = this % 1f
