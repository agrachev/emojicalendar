package ru.agrachev.emojicalendar.presentation.core

internal interface UIModelProvider<T> {

    fun provideInstance(): T
}
