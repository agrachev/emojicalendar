package ru.agrachev.calendar.data.repository

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.util.convertUUIDToByte
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.agrachev.calendar.data.database.EmojiCalendarDatabase
import ru.agrachev.calendar.data.mocks.newTestRule
import ru.agrachev.calendar.data.mocks.testRule
import ru.agrachev.calendar.data.typeconverter.LocalDateTimeConverter
import ru.agrachev.calendar.domain.model.CalendarRule
import strikt.api.Assertion
import strikt.api.DescribeableBuilder
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.single

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class EmojiCalendarEventsRepositoryTest {

    private val localDateTimeConverter = LocalDateTimeConverter()

    private lateinit var database: RoomDatabase
    private lateinit var repository: EmojiCalendarEventsRepository

    @Before
    fun initDatabase() {
        Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = EmojiCalendarDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
            .apply {
                database = this
                repository = EmojiCalendarEventsRepository(this, this.calendarRuleDao())
            }
    }

    @After
    fun teardownDatabase() {
        database.close()
    }

    @Test
    fun the_caller_receives_list_of_calendar_rules() = testDatabaseEvent(
        action = ::execInsertTestRuleSQL,
    ) {
        single()
            .get(CalendarRule::id)
            .isEqualTo(testRule.id)
    }

    @Test
    fun calendar_rule_insertion_adds_one_into_database() = testDatabaseEvent(
        action = {
            repository.pushCalendarRule(newTestRule)
        },
    ) {
        single()
            .get(CalendarRule::id)
            .isEqualTo(testRule.id)
    }

    @Test
    fun cancellation_removes_calendar_rule() = testDatabaseEvent(
        skip = 1,
        action = {
            newTestRule.also {
                repository.pushCalendarRule(it)
                repository.cancelCalendarRule(it)
            }
        },
    ) {
        isEmpty()
    }

    @Test
    fun cancellation_removes_event_from_parent_rule_event_list() = testDatabaseEvent(
        skip = 1,
        action = {
            newTestRule.also {
                repository.pushCalendarRule(it)
                repository.cancelCalendarEvent(it.calendarEvents.first())
            }
        },
    ) {
        single()
            .get(CalendarRule::calendarEvents)
            .isEmpty()
    }

    private inline fun <T> testDatabaseEvent(
        skip: Int = 0,
        crossinline action: suspend () -> Unit,
        crossinline builder: DescribeableBuilder<List<CalendarRule>>.() -> Assertion.Builder<T>,
    ) = runTest {
        val testJob = launch {
            val result = repository.getAllCalendarEvents()
                .drop(skip)
                .first()
            expectThat(result).builder()
        }
        action()
        testJob.join()
    }

    private suspend fun execInsertTestRuleSQL() = coroutineScope {
        newTestRule.apply {
            val writableDatabase = database.openHelper.writableDatabase
            database.runInTransaction {
                writableDatabase.execSQL(
                    sql = calendarRuleInsertQuery,
                    bindArgs = arrayOf(
                        convertUUIDToByte(id.value),
                        title,
                        localDateTimeConverter.toDateString(dateRange.start),
                        localDateTimeConverter.toDateString(dateRange.endInclusive),
                        recurrenceRule.toString(),
                    ),
                )
                this.calendarEvents.forEach {
                    writableDatabase.execSQL(
                        sql = calendarEventInsertQuery,
                        bindArgs = arrayOf(
                            convertUUIDToByte(it.id.value),
                            it.title,
                            it.emoji,
                            localDateTimeConverter.toDateString(it.scheduledDate),
                            it.rule?.id?.value?.let { uuid -> convertUUIDToByte(uuid) },
                        ),
                    )
                }
            }
        }
    }
}

private const val calendarRuleInsertQuery =
    "INSERT OR REPLACE INTO `calendar_rules` " +
            "(`rule_id`,`title`,`date_range_start`,`date_range_end_exclusive`,`recurrence_rule`) " +
            "VALUES (?,?,?,?,?)"

private const val calendarEventInsertQuery =
    "INSERT OR REPLACE INTO `calendar_events` " +
            "(`event_id`,`title`,`emoji`,`scheduled_date`,`parent_id`) " +
            "VALUES (?,?,?,?,?)"
