package ru.agrachev.calendar.domain.core

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isGreaterThanOrEqualTo
import strikt.assertions.isTrue
import java.lang.Math.random
import kotlin.math.abs
import kotlin.math.floor

const val RANDOM_LENGTH = 100

class ExtensionsTest {

    @Test
    fun `true to int returns 1`() {
        expectThat(true.toInt()).isEqualTo(1)
    }

    @Test
    fun `false to int returns 0`() {
        expectThat(false.toInt()).isEqualTo(0)
    }

    @Test
    fun `true to float returns 1f`() {
        expectThat(true.toFloat()).isEqualTo(1f)
    }

    @Test
    fun `false to float returns 0f`() {
        expectThat(false.toFloat()).isEqualTo(0f)
    }

    @Test
    fun `unary minus on int range returns inverted source range`() {
        val r1 = randomIntRange()
        expectThat(-r1)
            .get { start == -r1.first && endInclusive == -r1.last }.isTrue()
    }

    @Test
    fun `sum of 2 int ranges is a sum function on their edges`() {
        val r1 = randomIntRange()
        val r2 = randomIntRange()
        expectThat(r1 + r2)
            .get { start == r1.first + r2.first && endInclusive == r1.last + r2.last }
            .isTrue()
    }

    @Test
    fun `difference of 2 int ranges is a difference function on their edges`() {
        val r1 = randomIntRange()
        val r2 = randomIntRange()
        expectThat(r1 - r2)
            .get { start == r1.first - r2.first && endInclusive == r1.last - r2.last }
            .isTrue()
    }

    @Test
    fun `sum of an int range and int a sum function of the edge values and the int`() {
        val r1 = randomIntRange()
        val rInt = randomInt()
        expectThat(r1 + rInt)
            .get { start == r1.first + rInt && endInclusive == r1.last + rInt }
            .isTrue()
    }

    @Test
    fun `difference of an int range and int a sum function of the edge values and the int`() {
        val r1 = randomIntRange()
        val rInt = randomInt()
        expectThat(r1 - rInt)
            .get { start == r1.first - rInt && endInclusive == r1.last - rInt }
            .isTrue()
    }

    @Test
    fun `length of a range is a difference of its ranges`() {
        val r1 = randomIntRange()
        expectThat(r1.length).isEqualTo(abs(r1.last - r1.first))
    }

    @Test
    fun `length of a range is a non-negative value`() {
        val r1 = -randomIntRange()
        expectThat(r1.length).isGreaterThanOrEqualTo(0)
    }

    @Test
    fun `fraction of a float is between 0 and 1 exclusive`() {
        val float = (random() * RANDOM_LENGTH).toFloat()
        expectThat(float.fraction).isEqualTo(float - floor(float))
    }

    private fun randomIntRange() = randomInt().let {
        IntRange(it, it + randomInt())
    }

    private fun randomInt() = (random() * RANDOM_LENGTH).toInt()
}
