package ru.agrachev.calendardomain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.agrachev.calendardomain.model.CalendarRule
import ru.agrachev.calendardomain.repository.CalendarEventsRepository

class PushCalendarRuleUseCase(
    private val calendarEventsRepository: CalendarEventsRepository,
    private val dbCoroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(calendarRule: CalendarRule) =
        withContext(dbCoroutineDispatcher) {
            calendarEventsRepository.pushCalendarRule(calendarRule)
        }
}
