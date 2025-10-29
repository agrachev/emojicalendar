package ru.agrachev.calendarpresentation.core

internal interface UIModelProvider<T> {

    fun provideInstance(): T
}
