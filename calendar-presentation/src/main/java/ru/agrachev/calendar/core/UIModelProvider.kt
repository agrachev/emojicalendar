package ru.agrachev.presentation.core

internal interface UIModelProvider<T> {

    fun provideInstance(): T
}
