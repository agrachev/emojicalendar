package ru.agrachev.calendar.data.typeconverter

import org.junit.Test
import ru.agrachev.calendar.data.mocks.isoTestDate
import ru.agrachev.calendar.data.mocks.testDate
import strikt.api.expectCatching
import strikt.api.expectThat

class LocalDateTimeConverterTest {

    private val localDateTimeConverter = LocalDateTimeConverter()

    @Test
    fun `local date is properly formatted into ISO-8601 date stamp`() {
        expectCatching {
            val targetString = localDateTimeConverter.toDateString(testDate)
            expectThat(targetString).equals(isoTestDate)
        }
    }

    @Test
    fun `ISO-8601 date stamp is properly parsed into local date`() {
        expectCatching {
            val targetDate = localDateTimeConverter.toDate(isoTestDate)
            expectThat(targetDate).equals(testDate)
        }
    }
}
