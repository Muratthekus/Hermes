package me.thekusch.hermes.signup.ui.info

sealed class SignUpUiState {

    object Init: SignUpUiState()

    object Success: SignUpUiState()

    data class Error(val message: String?): SignUpUiState()

    object Loading: SignUpUiState()
}