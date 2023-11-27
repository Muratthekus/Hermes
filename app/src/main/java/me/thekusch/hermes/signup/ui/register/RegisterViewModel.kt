package me.thekusch.hermes.signup.ui.register

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.thekusch.hermes.core.datasource.local.HermesLocalDataSource
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val hermesLocalDataSource: HermesLocalDataSource
): ViewModel() {

    private val _finalizeProcess: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

    val finalizeProcess: StateFlow<Boolean> = _finalizeProcess

    fun finalizeSignUpProcess(
        username: String
    ) {
        hermesLocalDataSource.apply {
            this.name = username
            this.isSignUpProcessFinished = true
        }
        _finalizeProcess.value = true
    }
}