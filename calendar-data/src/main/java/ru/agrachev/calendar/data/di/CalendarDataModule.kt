package ru.agrachev.calendar.data.di

import androidx.room.Room
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.agrachev.calendar.data.dao.CalendarRuleDao
import ru.agrachev.calendar.data.database.EmojiCalendarDatabase
import ru.agrachev.calendar.data.repository.EmojiCalendarDataRepository
import ru.agrachev.calendar.data.repository.EmojiCalendarEventsRepository
import ru.agrachev.calendar.domain.repository.CalendarDataRepository
import ru.agrachev.calendar.domain.repository.CalendarEventsRepository

val calendarDataModule = module {
    single {
        Room.databaseBuilder(
            get(),
            EmojiCalendarDatabase::class.java, "emoji-calendar-db"
        ).build()
    }
    factory {
        get<EmojiCalendarDatabase>().calendarRuleDao()
    }
    singleOf<CalendarDataRepository>(::EmojiCalendarDataRepository)
    singleOf<CalendarEventsRepository, EmojiCalendarDatabase, CalendarRuleDao>(::EmojiCalendarEventsRepository)
}
