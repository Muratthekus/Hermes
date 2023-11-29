package me.thekusch.hermes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.thekusch.hermes.core.domain.SessionManager
import javax.inject.Inject

@HiltViewModel
class StarterViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isUserLoggedIn: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    val isUserLoggedIn: StateFlow<Boolean?> = _isUserLoggedIn.asStateFlow()

    fun checkIfUserLoggedIn() {
        viewModelScope.launch {
            delay(4000)
            _isUserLoggedIn.value = sessionManager.isUserLoggedIn()
        }
    }
}