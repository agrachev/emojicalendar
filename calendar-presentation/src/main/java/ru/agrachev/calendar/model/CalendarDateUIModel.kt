package ru.agrachev.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import ru.agrachev.calendardomain.core.DateRange
import ru.agrachev.calendardomain.core.length
import ru.agrachev.calendardomain.model.CalendarDate
import ru.agrachev.calendardomain.model.CalendarEvent
import ru.agrachev.calendardomain.model.CalendarRule
import ru.agrachev.calendardomain.model.Id
import ru.agrachev.calendardomain.model.RecurrenceRule
import ru.agrachev.presentation.core.dateItemIndex
import ru.agrachev.presentation.core.dateItemIndexes
import ru.agrachev.presentation.core.regularOffset
import ru.agrachev.presentation.core.regularOffsets
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val NOT_SET = ""

@Immutable
data class LocalizedCalendarResources(
    val weekdayNames: List<String>,
    val monthNames: List<String>,
)

@Stable
data class EmojiCalendarUIModel(
    val mainCalendarUIModel: MainCalendarUIModel,
    val eventsBrowserUIModel: EventsBrowserUIModel? = null,
)

@Stable
data class MainCalendarUIModel(
    val mainCalendarDateModelStorage: ru.agrachev.presentation.core.MainCalendarUIModelStorage,
    val monthOffsetKey: Int = 0,
    val calendarRefreshRequestToken: Id = Id.UNIQUE,
)

@Stable
data class MainCalendarDateUIModel(
    val date: LocalDate,
    val calendarEvents: List<CalendarEvent> = emptyList(),
) {
    val emoji by lazy {
        calendarEvents.firstOrNull()?.emoji
    }
}

@Stable
data class EventsBrowserUIModel(
    val dateModel: MainCalendarDateUIModel,
    val pendingRule: CalendarRuleUIModel? = null,
) {
    val scheduledEvents by dateModel::calendarEvents
}

@Stable
data class CalendarRuleUIModel(
    override val id: Id = Id.UNIQUE,
    override val title: String = NOT_SET,
    override val dateRangeOffsetIndexes: IntRange = (0..1).dateItemIndexes,
    override val calendarEventsUiModels: Set<CalendarEventUIModel> = emptySet(),
    override val recurrenceRule: RecurrenceRule = RecurrenceRule.DEFAULT,
) : CalendarRuleUILayout {

    fun getCalendarEventForIndex(index: Int) = calendarEventsUiModels.find {
        when (recurrenceRule) {
            RecurrenceRule.NONE -> it.dateIndex == index

            RecurrenceRule.PERIOD -> dateRangeOffsetIndexes.length
                .let { rangeLength ->
                    index % rangeLength == it.dateIndex % rangeLength
                }

            RecurrenceRule.WEEK -> (it.dateIndex - index) % 7 == 0

            else -> {
                it.dateIndex == index
            }
        }
    }
}

fun emptyCalendarRuleUIModel(origin: LocalDate = LocalDate.now()) =
    with(ChronoUnit.DAYS.between(LocalDate.now(), origin).toInt()) {
        CalendarRuleUIModel(
            dateRangeOffsetIndexes = (this..this + 1).dateItemIndexes,
        )
    }

typealias TitleType = String?
typealias EmojiType = String

@Stable
data class CalendarEventUIModel(
    val id: Id = DEFAULT_ID,
    val title: TitleType = DEFAULT_TITLE,
    val emoji: EmojiType = DEFAULT_EMOJI,
    val dateIndex: Int = DEFAULT_DATE_INDEX,
) {
    internal companion object Defaults {
        val DEFAULT_ID: Id
            get() = Id.UNIQUE
        val DEFAULT_TITLE: TitleType = null
        const val DEFAULT_EMOJI: EmojiType = NOT_SET
        val DEFAULT_DATE_INDEX: Int = 0.dateItemIndex
    }
}

internal fun CalendarRuleUIModel.toDomainModel(origin: LocalDate = LocalDate.now()) = CalendarRule(
    id = this.id,
    title = this.title,
    dateRange = origin.let {
        val regularOffsets = this.dateRangeOffsetIndexes.regularOffsets
        DateRange.of(
            it.plusDays(regularOffsets.start.toLong()),
            it.plusDays(regularOffsets.endInclusive.toLong())
        )
    },
    calendarEvents = this.calendarEventsUiModels
        .asSequence()
        .filter { it.emoji.isNotBlank() }
        .map { it.toDomainModel() }
        .toList(),
    recurrenceRule = this.recurrenceRule
)

internal fun CalendarEventUIModel.toDomainModel(origin: LocalDate = LocalDate.now()) =
    CalendarEvent(
        id = this.id,
        title = this.title,
        emoji = this.emoji,
        scheduledDate = origin
            .plusDays(this.dateIndex.regularOffset.toLong()),
    )

internal fun CalendarRule.toPresentationModel(origin: LocalDate = LocalDate.now()) =
    CalendarRuleUIModel(
        id = this.id,
        title = this.title,
        dateRangeOffsetIndexes = (ChronoUnit.DAYS.between(
            origin,
            this.dateRange.start,
        ).toInt()..(ChronoUnit.DAYS.between(
            origin,
            this.dateRange.endInclusive,
        ) + 1L).toInt()).dateItemIndexes,
        calendarEventsUiModels = this.calendarEvents.map { event ->
            event.toPresentationModel(origin)
        }.toSet(),
        recurrenceRule = this.recurrenceRule,
    )

internal fun CalendarEvent.toPresentationModel(origin: LocalDate = LocalDate.now()) =
    CalendarEventUIModel(
        id = this.id,
        title = this.title,
        emoji = this.emoji,
        dateIndex = ChronoUnit.DAYS.between(
            origin,
            this.scheduledDate,
        ).dateItemIndex,
    )

internal fun CalendarDate.toPresentationModel() = MainCalendarDateUIModel(
    date = this.date,
    calendarEvents = this.scheduledEvents,
)

interface CalendarRuleUILayout {
    val id: Id
    val title: String
    val dateRangeOffsetIndexes: IntRange
    val calendarEventsUiModels: Set<CalendarEventUIModel>
    val recurrenceRule: RecurrenceRule
}
