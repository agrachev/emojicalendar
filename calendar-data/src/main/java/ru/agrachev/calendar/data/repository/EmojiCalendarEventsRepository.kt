package ru.agrachev.calendar.data.repository

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.flow.map
import ru.agrachev.calendar.data.dao.CalendarRuleDao
import ru.agrachev.calendar.data.entity.toCalendarEventEntity
import ru.agrachev.calendar.data.entity.toCalendarRule
import ru.agrachev.calendar.data.entity.toCalendarRuleEntity
import ru.agrachev.calendar.domain.model.CalendarEvent
import ru.agrachev.calendar.domain.model.CalendarRule
import ru.agrachev.calendar.domain.repository.CalendarEventsRepository

class EmojiCalendarEventsRepository(
    private val database: RoomDatabase,
    private val calendarRuleDao: CalendarRuleDao,
) : CalendarEventsRepository {

    private val eventsFlow = calendarRuleDao
        .getAllCalendarRules()
        .map { rules ->
            rules.map { it.toCalendarRule() }
        }

    override fun getAllCalendarEvents() = eventsFlow

    override suspend fun pushCalendarRule(calendarRule: CalendarRule) =
        database.withTransaction {
            calendarRuleDao.run {
                val calendarRuleEntity = calendarRule.toCalendarRuleEntity()
                pushCalendarRule(calendarRuleEntity)
                pushCalendarEvents(calendarRule.calendarEvents.map {
                    it.toCalendarEventEntity(calendarRuleEntity.ruleId)
                })
            }
        }

    override suspend fun updateCalendarEvent(calendarEvent: CalendarEvent) =
        calendarRuleDao.updateCalendarEvent(
            calendarEvent.toCalendarEventEntity()
        )

    override suspend fun cancelCalendarEventOccurrence(calendarEvent: CalendarEvent) =
        calendarRuleDao.deleteCalendarEvent(
            calendarEvent.toCalendarEventEntity()
        )

    override suspend fun cancelCalendarRule(calendarRule: CalendarRule) =
        calendarRuleDao.deleteCalendarRule(
            calendarRule.toCalendarRuleEntity(),
        )
}
