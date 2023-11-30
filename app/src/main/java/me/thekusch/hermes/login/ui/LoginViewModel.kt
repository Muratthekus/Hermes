package me.thekusch.hermes.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.thekusch.hermes.core.datasource.local.model.Result
import me.thekusch.hermes.login.domain.LoginUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginUiState: MutableStateFlow<LoginUiState> =
        MutableStateFlow(LoginUiState.Init)

    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            loginUseCase.login(email, password).collectLatest {
                when (it) {
                    is Result.Started -> {
                        _loginUiState.value = LoginUiState.Loading
                    }

                    is Result.Success -> {
                        _loginUiState.value = LoginUiState.Success
                    }

                    is Result.Fail -> {
                        _loginUiState.value = LoginUiState.Error(it.exception.localizedMessage)
                    }

                    else -> {
                        _loginUiState.value = LoginUiState.Error()
                    }
                }
            }
        }

    }

}