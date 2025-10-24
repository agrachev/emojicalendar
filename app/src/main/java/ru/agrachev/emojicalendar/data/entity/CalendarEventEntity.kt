package ru.agrachev.emojicalendar.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import ru.agrachev.emojicalendar.domain.model.CalendarEvent
import ru.agrachev.emojicalendar.domain.model.Id
import java.time.LocalDate


@Entity(
    tableName = "calendar_events",
    foreignKeys = [
        ForeignKey(
            entity = CalendarRuleEntity::class,
            parentColumns = ["rule_id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class CalendarEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "event_id") val eventId: Id,
    val title: String?,
    val emoji: String,
    @ColumnInfo(name = "scheduled_date") val scheduledDate: LocalDate,
    @ColumnInfo(name = "parent_id") val parentId: Id,
)

fun CalendarEventEntity.toCalendarEvent() = CalendarEvent(
    id = this.eventId,
    title = this.title,
    emoji = this.emoji,
    scheduledDate = scheduledDate,
)

fun CalendarEvent.toCalendarEventEntity(parentId: Id) = CalendarEventEntity(
    eventId = this.id,
    title = this.title,
    emoji = this.emoji,
    scheduledDate = this.scheduledDate,
    parentId = parentId,
)

class LocalDateTimeConverter {
    @TypeConverter
    fun toDate(dateString: String): LocalDate = LocalDate.parse(dateString)

    @TypeConverter
    fun toDateString(date: LocalDate) = date.toString()
}
