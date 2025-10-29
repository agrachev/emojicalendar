package ru.agrachev.calendar.data.repository

import org.junit.Test
import ru.agrachev.calendar.data.mocks.availableLocales
import ru.agrachev.calendar.data.mocks.testLocales
import ru.agrachev.calendar.domain.core.Constants
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class EmojiCalendarDataRepositoryTest {

    private val dateRepository = EmojiCalendarDataRepository()

    @Test
    fun `count of month names is valid and consistent all available locales`() {
        val isMonthCountValidAndConsistent = availableLocales.all { locale ->
            dateRepository.getLocalizedMonthNames(locale).size == Constants.MONTH_COUNT
        }
        expectThat(isMonthCountValidAndConsistent).isTrue()
    }

    @Test
    fun `count of week day names is valid and consistent for all available locales`() {
        val isWeekDayCountValidAndConsistent = availableLocales.all { locale ->
            dateRepository.getLocalizedWeekdayNames(locale).size == Constants.WEEK_DAY_COUNT
        }
        expectThat(isWeekDayCountValidAndConsistent).isTrue()
    }

    @Test
    fun `month names output is different for different locales`() {
        assertArrayOutputUnique(*testLocales) {
            dateRepository.getLocalizedMonthNames(it)
        }
    }

    @Test
    fun `week day names output is different for different locales`() {
        assertArrayOutputUnique(*testLocales) {
            dateRepository.getLocalizedWeekdayNames(it)
        }
    }

    @Test
    fun `month data payload contains non fractional number of weeks`() {
        isConditionTrueForAllLocaleAndMonthOffsetCombo { dates, _, _ ->
            with(dates.size) {
                this > 0 && this % Constants.WEEK_DAY_COUNT == 0
            }
        }
    }

    @Test
    fun `dates in a month data payload are arranged evenly with a 1 day step`() {
        isConditionTrueForAllLocaleAndMonthOffsetCombo { dates, _, _ ->
            (1..dates.lastIndex).all {
                ChronoUnit.DAYS.between(dates[it - 1], dates[it]) == 1L
            }
        }
    }

    @Test
    fun `a calendar beginning of the month appears in the very first 7 payload items`() {
        isConditionTrueForAllLocaleAndMonthOffsetCombo { dates, testDate, monthOffset ->
            val firstDayOfMonth = with(testDate.plusMonths(monthOffset.toLong())) {
                YearMonth.of(this.year, this.month).atDay(1)
            }
            dates
                .take(Constants.WEEK_DAY_COUNT)
                .indexOf(firstDayOfMonth) >= 0
        }
    }

    private inline fun <T> assertArrayOutputUnique(
        vararg input: T,
        dataProvider: (T) -> Iterable<*>,
    ) {
        val uniqueOutputs = input.mapNotNull {
            dataProvider(it).hashCode()
        }
            .groupBy { it }
            .size
        expectThat(input.size).isEqualTo(uniqueOutputs)
    }

    private inline fun isConditionTrueForAllLocaleAndMonthOffsetCombo(
        condition: (dates: List<LocalDate>, testDate: LocalDate, monthOffset: Int) -> Boolean,
    ) {
        val testDate = LocalDate.of(2025, 1, 1)
        val areAllConditionsTrue =
            testLocales.combine(-Constants.MONTH_COUNT / 2..<Constants.MONTH_COUNT / 2) { locale, monthOffset ->
                condition(
                    dateRepository
                        .requestDates(monthOffset, testDate, locale),
                    testDate,
                    monthOffset,
                )
            }
                .asSequence()
                .all { it }
        expectThat(areAllConditionsTrue).isTrue()
    }

    private inline fun <T, R, V> Array<out T>.combine(
        other: Iterable<R>,
        transform: (e1: T, e2: R) -> V,
    ): List<V> {
        val list = ArrayList<V>(size * other.count())
        for (e1 in this) {
            for (e2 in other) {
                list.add(transform(e1, e2))
            }
        }
        return list
    }
}
