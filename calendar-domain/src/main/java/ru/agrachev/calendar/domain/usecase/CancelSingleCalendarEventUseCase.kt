package ru.agrachev.calendar.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.agrachev.calendar.domain.model.CalendarEvent
import ru.agrachev.calendar.domain.repository.CalendarEventsRepository
import java.time.LocalDate

class CancelSingleCalendarEventUseCase(
    private val calendarEventsRepository: CalendarEventsRepository,
    private val dbCoroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(calendarEvent: CalendarEvent, date: LocalDate) =
        withContext(dbCoroutineDispatcher) {
            calendarEventsRepository.updateCalendarEvent(
                calendarEvent.copy(

                )
            )
        }
}
