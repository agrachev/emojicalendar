package ru.agrachev.calendar.domain.usecase

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.agrachev.calendar.domain.mocks.testDispatcher
import ru.agrachev.calendar.domain.mocks.testRule
import ru.agrachev.calendar.domain.repository.CalendarEventsRepository
import strikt.api.expectDoesNotThrow
import strikt.api.expectThrows

class PushCalendarRuleUseCaseTest {

    @MockK
    private lateinit var calendarEventsRepository: CalendarEventsRepository
    private lateinit var pushCalendarRuleUseCase: PushCalendarRuleUseCase

    @Before
    fun beforeTest() {
        MockKAnnotations.init(this)
        pushCalendarRuleUseCase = PushCalendarRuleUseCase(
            calendarEventsRepository, testDispatcher,
        )
    }

    @Test
    fun `completes normally`() = runTest {
        coEvery { calendarEventsRepository.pushCalendarRule(any()) } just runs
        expectDoesNotThrow {
            pushCalendarRuleUseCase.invoke(testRule)
        }
        coVerify { calendarEventsRepository.pushCalendarRule(any()) }
    }

    @Test
    fun `throws an exception if any`() = runTest {
        coEvery { calendarEventsRepository.pushCalendarRule(any()) } throws Exception()
        expectThrows<Exception> {
            pushCalendarRuleUseCase.invoke(testRule)
        }
        coVerify { calendarEventsRepository.pushCalendarRule(any()) }
    }
}
