package ru.agrachev.emojicalendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states

open class BaseMviViewModel<in I : Any, out M : Any, out L : Any>(
    private val store: Store<I, M, L>,
) : ViewModel() {

    val initialUiState by store::state
    val uiState
        get() = store.states
    val labels
        get() = store.labels

    fun accept(intent: I) = store.accept(intent)

    override fun onCleared() {
        super.onCleared()
        store.dispose()
    }
}
