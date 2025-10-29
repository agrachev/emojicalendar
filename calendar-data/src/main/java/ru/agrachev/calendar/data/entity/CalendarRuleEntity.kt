package ru.agrachev.calendar.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.agrachev.calendar.domain.core.DateRange
import ru.agrachev.calendar.domain.model.CalendarRule
import ru.agrachev.calendar.domain.model.Id
import ru.agrachev.calendar.domain.model.RecurrenceRule
import java.time.LocalDate

@Entity(tableName = "calendar_rules")
data class CalendarRuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "rule_id") val ruleId: Id,
    val title: String,
    @ColumnInfo(name = "date_range_start") val dateRangeStart: LocalDate,
    @ColumnInfo(name = "date_range_end_exclusive") val dateRangeEnd: LocalDate,
    @ColumnInfo(name = "recurrence_rule") val recurrenceRule: RecurrenceRule,
)

data class RuleWithEvents(
    @Embedded val calendarRule: CalendarRuleEntity,
    @Relation(
        parentColumn = "rule_id",
        entityColumn = "parent_id"
    )
    val calendarEvents: List<CalendarEventEntity>
)

fun RuleWithEvents.toCalendarRule() = this.calendarRule.toCalendarRule(
    calendarEventEntities = this.calendarEvents,
)

fun CalendarRuleEntity.toCalendarRule(calendarEventEntities: List<CalendarEventEntity>) =
    CalendarRule(
        id = this.ruleId,
        title = this.title,
        dateRange = DateRange.of(
            this.dateRangeStart,
            this.dateRangeEnd,
        ),
        calendarEvents = calendarEventEntities.map { it.toCalendarEvent() },
        recurrenceRule = this.recurrenceRule,
    )

fun CalendarRule.toCalendarRuleEntity() = CalendarRuleEntity(
    ruleId = this.id,
    title = this.title,
    dateRangeStart = this.dateRange.start,
    dateRangeEnd = this.dateRange.end,
    recurrenceRule = this.recurrenceRule,
)
