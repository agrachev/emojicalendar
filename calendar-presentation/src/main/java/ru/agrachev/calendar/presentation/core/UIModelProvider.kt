package ru.agrachev.calendar.presentation.core

internal interface UIModelProvider<T> {

    fun provideInstance(): T
}
