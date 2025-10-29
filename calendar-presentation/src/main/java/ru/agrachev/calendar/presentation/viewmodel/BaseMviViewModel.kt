package ru.agrachev.calendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states

open class BaseMviViewModel<in I : Any, out M : Any, out L : Any>(
    private val store: Store<I, M, L>,
) : ViewModel(), MviStateHolder<I, M, L> {

    override val initialUiState by store::state
    override val uiState
        get() = store.states
    override val labels
        get() = store.labels

    override fun accept(intent: I) = store.accept(intent)

    override fun onCleared() {
        super.onCleared()
        store.dispose()
    }
}
