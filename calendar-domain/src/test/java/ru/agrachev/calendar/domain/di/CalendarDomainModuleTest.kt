package ru.agrachev.calendar.domain.di

import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.MissingKoinDefinitionException
import org.koin.test.verify.Verify.verify
import strikt.api.expectThrows

class CalendarDomainModuleTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `domain module validation throws an exception`() {
        // Expected since missing definitions are providing by other modules
        expectThrows<MissingKoinDefinitionException> {
            verify(calendarDomainModule)
        }
    }
}
