package ru.agrachev.calendar.domain.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.agrachev.calendar.domain.usecase.FetchMonthDataUseCase
import ru.agrachev.calendar.domain.usecase.PushCalendarRuleUseCase

val calendarDomainModule = module {
    single {
        Dispatchers.IO
    }
    singleOf(::FetchMonthDataUseCase)
    singleOf(::PushCalendarRuleUseCase)
}
