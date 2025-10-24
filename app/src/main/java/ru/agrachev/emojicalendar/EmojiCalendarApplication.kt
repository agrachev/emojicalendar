package ru.agrachev.emojicalendar

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.agrachev.emojicalendar.di.appModule


class EmojiCalendarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EmojiCalendarApplication)
            modules(appModule)
        }
    }
}
