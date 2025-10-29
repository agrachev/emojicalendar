package ru.agrachev.calendar.presentation.core

import com.google.common.cache.CacheBuilder

class MainCalendarDateUIModelStorage : MainCalendarUIModelStorage {

    private val cache = CacheBuilder
        .newBuilder()
        .maximumSize(CACHE_SIZE_THRESHOLD)
        .build<CalendarDateStorageKey, CalendarDateStorageValue>()

    override fun get(
        key: CalendarDateStorageKey,
    ): CalendarDateStorageValue? =
        cache.getIfPresent(key)

    override fun put(
        key: CalendarDateStorageKey,
        value: CalendarDateStorageValue,
    ): CalendarDateStorageValue? = value.apply {
        cache.put(key, this)
    }

    companion object {
        const val CACHE_SIZE_THRESHOLD = 10L
    }
}
