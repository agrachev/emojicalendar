package ru.agrachev.emojicalendar.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.agrachev.emojicalendar.data.entity.CalendarEventEntity
import ru.agrachev.emojicalendar.data.entity.CalendarRuleEntity
import ru.agrachev.emojicalendar.data.entity.LocalDateTimeConverter

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
