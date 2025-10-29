package ru.agrachev.calendar.domain.usecase

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verifyAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import ru.agrachev.calendar.domain.mocks.testDate
import ru.agrachev.calendar.domain.mocks.testDates
import ru.agrachev.calendar.domain.mocks.testDispatcher
import ru.agrachev.calendar.domain.mocks.testEvent
import ru.agrachev.calendar.domain.mocks.testRule
import ru.agrachev.calendar.domain.model.CalendarDate
import ru.agrachev.calendar.domain.model.CalendarRule
import ru.agrachev.calendar.domain.repository.CalendarDataRepository
import ru.agrachev.calendar.domain.repository.CalendarEventsRepository
import strikt.api.Assertion
import strikt.api.DescribeableBuilder
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.allIndexed
import strikt.assertions.filter
import strikt.assertions.isContainedIn
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class FetchMonthDataUseCaseTest {

    @MockK
    private lateinit var calendarDataRepository: CalendarDataRepository

    @MockK
    private lateinit var calendarEventsRepository: CalendarEventsRepository
    private lateinit var fetchMonthDataUseCase: FetchMonthDataUseCase

    @Before
    fun beforeTest() {
        MockKAnnotations.init(this)
        fetchMonthDataUseCase = FetchMonthDataUseCase(
            calendarDataRepository, calendarEventsRepository, testDispatcher,
        )
    }

    @Test
    fun `calendar events preserve calendar natural order`() = runTest {
        expectCalendarDates {
            allIndexed { index ->
                get(CalendarDate::date).isEqualTo(testDates.getOrNull(index))
            }
        }
    }

    @Test
    fun `no scheduled events in case if no events are stored`() = runTest {
        expectCalendarDates {
            all { get(CalendarDate::scheduledEvents).isEmpty() }
        }
    }

    @Test
    fun `scheduled events are properly associated with stored events`() = runTest {
        val range = 0..testDates.lastIndex
        val randomCalendarDates = range
            .take(range.random())
            .map { range.random() }
            .distinct()
            .sorted()
            .map { testDates[it] }
        val randomCalendarRules = randomCalendarDates.map {
            testRule.copy(
                calendarEvents = listOf(
                    testEvent.copy(scheduledDate = it),
                ),
            )
        }
        expectCalendarDates(randomCalendarRules) {
            filter { it.scheduledEvents.isNotEmpty() }
                .all {
                    get { scheduledEvents.first().scheduledDate }.isContainedIn(
                        randomCalendarDates
                    )
                }
        }
    }

    private suspend inline fun <T> expectCalendarDates(
        calendarRules: List<CalendarRule> = emptyList(),
        builder: DescribeableBuilder<List<CalendarDate>>.() -> Assertion.Builder<T>,
    ) {
        val result = requestTestDates(calendarRules)
        expectThat(result).builder()
    }

    private suspend fun requestTestDates(
        calendarRules: List<CalendarRule> = emptyList(),
    ): List<CalendarDate> {
        every { calendarEventsRepository.getAllCalendarEvents() } returns flow {
            emit(calendarRules)
        }
        every { calendarDataRepository.requestDates(any(), testDate) } returns testDates
        return fetchMonthDataUseCase.invoke(0, testDate).also {
            verifyAll {
                calendarEventsRepository.getAllCalendarEvents()
                calendarDataRepository.requestDates(any(), testDate)
            }
        }
    }
}
