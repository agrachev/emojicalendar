package ru.agrachev.emojicalendar

import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import ru.agrachev.emojicalendar.data.repository.EmojiCalendarDataRepository
import ru.agrachev.emojicalendar.domain.repository.CalendarDataRepository
import java.time.DayOfWeek
import java.time.Month
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

    companion object {
        val MONTH_COUNT = Month.entries.size
        val WEEK_DAY_COUNT = DayOfWeek.entries.size
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
