package ru.agrachev.emojicalendar.presentation.core

import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import java.util.concurrent.ConcurrentHashMap

class MainCalendarDateUIModelStorage : MainCalendarUIModelStorage {

    private val cache =
        ConcurrentHashMap<Int, List<MainCalendarDateUIModel>>()

    override fun get(key: Int): List<MainCalendarDateUIModel>? =
        cache.get(key)

    override fun put(
        key: Int, value: List<MainCalendarDateUIModel>
    ): List<MainCalendarDateUIModel>? = value.apply {
        cache.put(key, this)
        println("ZZZZZZZZZZZ ${cache.keys}")
    }

    override fun remove(key: Int) {
        cache.remove(key)
    }
}
