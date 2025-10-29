package ru.agrachev.calendar.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.agrachev.calendar.domain.model.CalendarRule
import ru.agrachev.calendar.domain.repository.CalendarEventsRepository

class PushCalendarRuleUseCase(
    private val calendarEventsRepository: CalendarEventsRepository,
    private val dbCoroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(calendarRule: CalendarRule) =
        withContext(dbCoroutineDispatcher) {
            calendarEventsRepository.pushCalendarRule(calendarRule)
        }
}
