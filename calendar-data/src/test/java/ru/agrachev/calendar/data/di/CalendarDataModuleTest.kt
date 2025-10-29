package ru.agrachev.calendar.data.di

import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.Verify.verify
import strikt.api.expectDoesNotThrow

class CalendarDataModuleTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `data module validation does not throw any exceptions`() {
        expectDoesNotThrow {
            verify(calendarDataModule)
        }
    }
}
