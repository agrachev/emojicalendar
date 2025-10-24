package ru.agrachev.emojicalendar.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.agrachev.emojicalendar.domain.model.CalendarRule
import ru.agrachev.emojicalendar.domain.repository.CalendarEventsRepository

class PushCalendarRuleUseCase(
    private val calendarEventsRepository: CalendarEventsRepository,
    private val dbCoroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(calendarRule: CalendarRule) =
        withContext(dbCoroutineDispatcher) {
            calendarEventsRepository.pushCalendarRule(calendarRule)
        }
}
