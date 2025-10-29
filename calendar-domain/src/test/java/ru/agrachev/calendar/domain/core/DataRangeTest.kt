package ru.agrachev.calendar.domain.core

import org.junit.Test
import ru.agrachev.calendar.domain.mocks.testDate
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isTrue
import java.time.DateTimeException

class DataRangeTest {

    @Test
    fun `range is valid when the end date is after the start`() {
        testDate.plusDays(1).also { nextDay ->
            expectThat(DateRange.of(testDate, nextDay))
                .get { start == testDate && end == nextDay }.isTrue()
        }
    }

    @Test
    fun `range is valid when the end date is the same as the start`() {
        testDate.plusDays(0).also { sameDay ->
            expectThat(DateRange.of(testDate, sameDay))
                .get { start == testDate && end == sameDay }.isTrue()
        }
    }

    @Test
    fun `range throws exception when the start day is before the end date`() {
        testDate.plusDays(-1).also { previousDay ->
            expectThrows<DateTimeException> {
                DateRange.of(testDate, previousDay)
            }
        }
    }
}
