package me.thekusch.hermes.core.common.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

/**
* Extension function for StateFlow to be able to rollback previous value of state flow.
 * [StateHistoryWrapper] keep previous and current value.
* */
fun <T> StateFlow<T>.withHistory(
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly
): StateFlow<StateHistoryWrapper<T>?> =
    this.runningFold(
        initial = StateHistoryWrapper(
            null,
            this.value
        )
    ) { previous, current ->
        StateHistoryWrapper(previous.current, current)
    }.distinctUntilChanged()
        .stateIn(scope, sharingStarted, null)

data class StateHistoryWrapper<T>(
    val previous: T?,
    val current: T
)