package ru.agrachev.calendar.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.agrachev.calendar.data.dao.CalendarRuleDao
import ru.agrachev.calendar.data.entity.CalendarEventEntity
import ru.agrachev.calendar.data.entity.CalendarRuleEntity
import ru.agrachev.calendar.data.typeconverter.LocalDateTimeConverter

@Database(
    version = 1,
    entities = [
        CalendarRuleEntity::class,
        CalendarEventEntity::class,
    ],
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class EmojiCalendarDatabase : RoomDatabase() {
    abstract fun calendarRuleDao(): CalendarRuleDao
}
