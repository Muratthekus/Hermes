package me.thekusch.hermes.signup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.thekusch.hermes.core.datasource.local.HermesLocalDataSource
import me.thekusch.hermes.supabase.Supabase
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val supabase: Supabase
) : ViewModel() {


    private val _signUpUiState: MutableStateFlow<SignUpUiState> =
        MutableStateFlow(SignUpUiState.Init)

    val signUpUiState: StateFlow<SignUpUiState> = _signUpUiState

    fun signUpUser(email: String, password: String) {
        viewModelScope.launch {
            _signUpUiState.value = SignUpUiState.Loading
            val signUpResultDef = async {
                supabase.signupUser(email, password)
            }

            val signUpResult = signUpResultDef.await()

            HermesLocalDataSource.apply {
                this.email = signUpResult
                this.password = password
            }
            _signUpUiState.value = SignUpUiState.Success
        }
    }
}