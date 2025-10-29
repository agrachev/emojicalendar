package ru.agrachev.calendarpresentation.viewmodel

import kotlinx.coroutines.flow.Flow

interface MviStateHolder<in I : Any, out M : Any, out L : Any> {
    val initialUiState: M
    val uiState: Flow<M>
    val labels: Flow<L>
    fun accept(intent: I)
}
