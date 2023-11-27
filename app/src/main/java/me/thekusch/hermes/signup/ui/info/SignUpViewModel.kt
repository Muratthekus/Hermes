package me.thekusch.hermes.signup.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.thekusch.hermes.signup.domain.SignUpUseCase
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {


    private val _signUpUiState: MutableStateFlow<SignUpUiState> =
        MutableStateFlow(SignUpUiState.Init)

    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState

    fun signUpUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signUpUiState.value = SignUpUiState.Loading
            signUpUseCase.signUpUser(email, password, name)
            _signUpUiState.value = SignUpUiState.Success
        }
    }
}