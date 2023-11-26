package me.thekusch.hermes.signup.ui

sealed class SignUpUiState {

    object Init: SignUpUiState()

    object Success: SignUpUiState()

    object Error: SignUpUiState()

    object Loading: SignUpUiState()
}