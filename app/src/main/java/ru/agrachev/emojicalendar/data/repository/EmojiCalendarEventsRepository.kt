package ru.agrachev.emojicalendar.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.map
import ru.agrachev.emojicalendar.data.dao.EmojiCalendarDatabase
import ru.agrachev.emojicalendar.data.entity.toCalendarEventEntity
import ru.agrachev.emojicalendar.data.entity.toCalendarRule
import ru.agrachev.emojicalendar.data.entity.toCalendarRuleEntity
import ru.agrachev.emojicalendar.domain.model.CalendarEvent
import ru.agrachev.emojicalendar.domain.model.CalendarRule
import ru.agrachev.emojicalendar.domain.model.Id
import ru.agrachev.emojicalendar.domain.repository.CalendarEventsRepository

class EmojiCalendarEventsRepository(
    private val database: EmojiCalendarDatabase,
) : CalendarEventsRepository {

    private val eventsFlow = database.calendarRuleDao()
        .getAllCalendarRules()
        .map { rules ->
            rules.map { it.toCalendarRule() }
        }

    override fun getAllCalendarEvents() = eventsFlow

    override suspend fun pushCalendarRule(calendarRule: CalendarRule) =
        database.withTransaction {
            database.calendarRuleDao().run {
                val calendarRuleEntity = calendarRule.toCalendarRuleEntity()
                pushCalendarRule(calendarRuleEntity)
                pushCalendarEvents(calendarRule.calendarEvents.map {
                    it.toCalendarEventEntity(calendarRuleEntity.ruleId)
                })
            }
        }

    override suspend fun cancelCalendarEvent(calendarEvent: CalendarEvent) =
        database.calendarRuleDao().deleteCalendarEvent(
            calendarEvent.toCalendarEventEntity(
                parentId = calendarEvent.rule?.id ?: Id.UNIQUE
            )
        )

    override suspend fun cancelCalendarRule(calendarRule: CalendarRule) =
        database.calendarRuleDao().deleteCalendarRule(
            calendarRule.toCalendarRuleEntity()
        )
}
