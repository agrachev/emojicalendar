package ru.agrachev.calendar.presentation.di

import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.agrachev.calendar.presentation.arch.EmojiCalendarStoreFactory
import ru.agrachev.calendar.presentation.core.EmojiCalendarUIModelProvider
import ru.agrachev.calendar.presentation.core.MainCalendarDateUIModelStorage
import ru.agrachev.calendar.presentation.core.MainCalendarUIModelStorage
import ru.agrachev.calendar.presentation.core.UIModelProvider
import ru.agrachev.calendar.presentation.model.EmojiCalendarUIModel
import ru.agrachev.calendar.presentation.viewmodel.CalendarViewModel
import ru.agrachev.calendar.presentation.widget.slider.DateRangeThumbState
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

val calendarPresentationModule = module {
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
