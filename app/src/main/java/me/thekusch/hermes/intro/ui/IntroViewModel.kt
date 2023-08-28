package me.thekusch.hermes.intro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.thekusch.hermes.intro.domain.IntroUseCase
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val introUseCase: IntroUseCase
) : ViewModel() {

    private val _introUiState: MutableStateFlow<IntroUiState> =
        MutableStateFlow(IntroUiState.Loading)

    val introUiState: StateFlow<IntroUiState> = _introUiState

    fun getIntroState() {
        viewModelScope.launch {
            val introItems = introUseCase.provideIntroItems()
            _introUiState.value = IntroUiState.Result(introItems)
        }
    }

}