package ru.agrachev.calendar.domain.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.agrachev.calendar.domain.usecase.FetchMonthDataUseCase
import ru.agrachev.calendar.domain.usecase.PushCalendarRuleUseCase

val calendarDomainModule = module {
    single(qualifier = named(CoroutineDispatcher.DATABASE)) {
        Dispatchers.IO
    }
    single {
        FetchMonthDataUseCase(
            calendarDataRepository = get(),
            calendarEventsRepository = get(),
            dbCoroutineDispatcher = get(named(CoroutineDispatcher.DATABASE)),
        )
    }
    single {
        PushCalendarRuleUseCase(
            calendarEventsRepository = get(),
            dbCoroutineDispatcher = get(named(CoroutineDispatcher.DATABASE)),
        )
    }
}

private enum class CoroutineDispatcher {
    DATABASE,
}
