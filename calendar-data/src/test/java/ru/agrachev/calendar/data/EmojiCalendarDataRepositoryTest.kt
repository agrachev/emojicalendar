package ru.agrachev.calendar.data

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import ru.agrachev.calendar.domain.core.Constants.MONTH_COUNT
import ru.agrachev.calendar.domain.core.Constants.WEEK_DAY_COUNT
import ru.agrachev.calendar.domain.repository.CalendarDataRepository
import ru.agrachev.calendar.data.repository.EmojiCalendarDataRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.Locale

class EmojiCalendarDataRepositoryTest {

    @Test
    fun `count of month names is valid and consistent all available locales`() {
        val isMonthCountValidAndConsistent = AVAILABLE_LOCALES.all { locale ->
            dateRepository.getLocalizedMonthNames(locale).size == MONTH_COUNT
        }
        Assert.assertTrue(isMonthCountValidAndConsistent)
    }

    @Test
    fun `count of week day names is valid and consistent for all available locales`() {
        val isWeekDayCountValidAndConsistent = AVAILABLE_LOCALES.all { locale ->
            dateRepository.getLocalizedWeekdayNames(locale).size == WEEK_DAY_COUNT
        }
        Assert.assertTrue(isWeekDayCountValidAndConsistent)
    }

    @Test
    fun `month names output is different for different locales`() {
        assertArrayOutputUnique(*TEST_LOCALES) {
            dateRepository.getLocalizedMonthNames(it)
        }
    }

    @Test
    fun `week day names output is different for different locales`() {
        assertArrayOutputUnique(*TEST_LOCALES) {
            dateRepository.getLocalizedWeekdayNames(it)
        }
    }

    @Test
    fun `month data payload contains non fractional number of weeks`() {
        isConditionTrueForAllLocaleAndMonthOffsetCombo { dates, _, _ ->
            with(dates.size) {
                this > 0 && this % WEEK_DAY_COUNT == 0
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
                .take(WEEK_DAY_COUNT)
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
        Assert.assertSame(input.size, uniqueOutputs)
    }

    private inline fun isConditionTrueForAllLocaleAndMonthOffsetCombo(
        condition: (dates: List<LocalDate>, testDate: LocalDate, monthOffset: Int) -> Boolean,
    ) {
        val testDate = LocalDate.of(2025, 1, 1)
        val areAllConditionsTrue =
            TEST_LOCALES.combine(-MONTH_COUNT / 2..<MONTH_COUNT / 2) { locale, monthOffset ->
                condition(
                    dateRepository
                        .requestDates(monthOffset, testDate, locale),
                    testDate,
                    monthOffset,
                )
            }
                .asSequence()
                .all { it }
        Assert.assertTrue(areAllConditionsTrue)
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

    companion object {
        val AVAILABLE_LOCALES = Locale.getAvailableLocales()
            .filterNotNull()
        val TEST_LOCALES = listOf("us", "ru")
            .mapNotNull {
                Locale.forLanguageTag(it)
            }.toTypedArray()

        lateinit var dateRepository: CalendarDataRepository

        @BeforeClass
        @JvmStatic
        fun initDateRepository() {
            dateRepository = EmojiCalendarDataRepository()
        }
    }
}