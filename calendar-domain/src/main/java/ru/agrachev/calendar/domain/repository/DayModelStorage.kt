package ru.agrachev.calendar.domain.repository

interface DayModelStorage<K, V : List<*>> {

    operator fun get(key: K): V?

    fun put(key: K, value: V): V?
}
