package ru.agrachev.emojicalendar.di

import org.koin.dsl.module
import ru.agrachev.calendar.data.di.calendarDataModule
import ru.agrachev.calendar.domain.di.calendarDomainModule
import ru.agrachev.calendar.presentation.di.calendarPresentationModule

val appModule = module {
    includes(
        calendarDomainModule,
        calendarDataModule,
        calendarPresentationModule,
    )
}
