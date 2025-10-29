package ru.agrachev.calendar.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import ru.agrachev.calendar.domain.model.CalendarDate
import ru.agrachev.calendar.domain.repository.CalendarDataRepository
import ru.agrachev.calendar.domain.repository.CalendarEventsRepository
import java.time.LocalDate

class FetchMonthDataUseCase(
    private val calendarDataRepository: CalendarDataRepository,
    private val calendarEventsRepository: CalendarEventsRepository,
    private val dbCoroutineDispatcher: CoroutineDispatcher,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        offsetFromCurrentMonth: Int,
        originDate: LocalDate = LocalDate.now(),
    ): List<CalendarDate> =
        flowOf(calendarDataRepository.requestDates(offsetFromCurrentMonth, originDate))
            .zip(
                calendarEventsRepository.getAllCalendarEvents()
                    .flowOn(dbCoroutineDispatcher)
            ) { dates, rules ->
                dates
                    .asFlow()
                    .flatMapMerge { localDate ->
                        flow {
                            emit(
                                CalendarDate(
                                    date = localDate,
                                    scheduledEvents = rules.mapNotNull { event ->
                                        event.getMatchingEvent(localDate)
                                            ?.copy(rule = event)
                                    }
                                )
                            )
                        }
                    }
                    .toList()
                    .sortedBy { it.date }
            }
            .single()

}
