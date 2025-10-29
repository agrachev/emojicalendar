package ru.agrachev.calendar.data.typeconverter

import org.junit.Test
import ru.agrachev.calendar.data.mocks.isoTestDate
import ru.agrachev.calendar.data.mocks.testDate
import strikt.api.expectCatching
import strikt.api.expectThat

class LocalDateTimeConverterTest {

    val localDateTimeConverter = LocalDateTimeConverter()

    @Test
    fun aaa() {
        expectCatching {
            val targetString = localDateTimeConverter.toDateString(testDate)
            expectThat(targetString).equals(isoTestDate)
        }
    }

    @Test
    fun bbb() {
        expectCatching {
            val targetDate = localDateTimeConverter.toDate(isoTestDate)
            expectThat(targetDate).equals(testDate)
        }
    }
}
