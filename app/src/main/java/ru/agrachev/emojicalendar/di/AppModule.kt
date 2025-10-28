package ru.agrachev.emojicalendar.di

import androidx.room.Room
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.agrachev.emojicalendar.data.dao.EmojiCalendarDatabase
import ru.agrachev.emojicalendar.data.repository.EmojiCalendarDataRepository
import ru.agrachev.emojicalendar.data.repository.EmojiCalendarEventsRepository
import ru.agrachev.emojicalendar.domain.repository.CalendarDataRepository
import ru.agrachev.emojicalendar.domain.repository.CalendarEventsRepository
import ru.agrachev.emojicalendar.domain.usecase.FetchMonthDataUseCase
import ru.agrachev.emojicalendar.domain.usecase.PushCalendarRuleUseCase
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarStoreFactory
import ru.agrachev.emojicalendar.presentation.core.EmojiCalendarUIModelProvider
import ru.agrachev.emojicalendar.presentation.core.MainCalendarDateUIModelStorage
import ru.agrachev.emojicalendar.presentation.core.MainCalendarUIModelStorage
import ru.agrachev.emojicalendar.presentation.core.UIModelProvider
import ru.agrachev.emojicalendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.emojicalendar.presentation.viewmodel.CalendarViewModel
import ru.agrachev.emojicalendar.presentation.widget.slider.DateRangeThumbState
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(KoinExperimentalAPI::class)
val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            EmojiCalendarDatabase::class.java, "emoji-calendar-db"
        ).build()
    }
    single(qualifier = named("aaa")) {
        Dispatchers.IO
    }
    single {
        FetchMonthDataUseCase(get(), get(), get(named("aaa")))
    }
    single {
        PushCalendarRuleUseCase(get(), get(named("aaa")))
    }
    singleOf<CalendarDataRepository>(::EmojiCalendarDataRepository)
    single<CalendarEventsRepository> {
        EmojiCalendarEventsRepository(get())
    }

    factoryOf<MainCalendarUIModelStorage>(::MainCalendarDateUIModelStorage)
    factory<UIModelProvider<EmojiCalendarUIModel>> {
        EmojiCalendarUIModelProvider(
            storage = get(),
        )
    }
    factory {
        val calendarViewUIModelProvider: UIModelProvider<EmojiCalendarUIModel> by inject()
        calendarViewUIModelProvider.provideInstance()
    }

    // TODO Should be scoped to viewmodel only
    factory {
        EmojiCalendarStoreFactory(
            storeFactory = DefaultStoreFactory(),
            pushCalendarRuleUseCase = get(),
            fetchMonthDataUseCase = get(),
            initialState = get(),
        ).create()
    }
    viewModelOf(::CalendarViewModel)

    factory {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    }

    factory { parameters ->
        DateRangeThumbState(parameters[0], parameters[1], get())
    }
}
