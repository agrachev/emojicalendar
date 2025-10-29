package ru.agrachev.calendar.presentation.viewmodel

import kotlinx.coroutines.flow.Flow

interface MviStateHolder<in I : Any, out M : Any, out L : Any> {
    val initialUiState: M
    val uiState: Flow<M>
    val labels: Flow<L>
    fun accept(intent: I)
}
