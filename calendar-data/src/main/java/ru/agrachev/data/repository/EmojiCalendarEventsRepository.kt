package ru.agrachev.calendardata.repository

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import kotlinx.coroutines.flow.map
import ru.agrachev.calendardata.dao.CalendarRuleDao
import ru.agrachev.calendardata.entity.toCalendarEventEntity
import ru.agrachev.calendardata.entity.toCalendarRule
import ru.agrachev.calendardata.entity.toCalendarRuleEntity
import ru.agrachev.domain.model.CalendarEvent
import ru.agrachev.domain.model.CalendarRule
import ru.agrachev.domain.model.Id
import ru.agrachev.domain.repository.CalendarEventsRepository

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

    override suspend fun cancelCalendarEvent(calendarEvent: CalendarEvent) =
        calendarRuleDao.deleteCalendarEvent(
            calendarEvent.toCalendarEventEntity(
                parentId = calendarEvent.rule?.id ?: Id.UNIQUE,
            )
        )

    override suspend fun cancelCalendarRule(calendarRule: CalendarRule) =
        calendarRuleDao.deleteCalendarRule(
            calendarRule.toCalendarRuleEntity(),
        )
}
