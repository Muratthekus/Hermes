package me.thekusch.hermes.signup.ui.info

sealed class SignUpUiState {

    object Init: SignUpUiState()

    object Success: SignUpUiState()

    object Error: SignUpUiState()

    object Loading: SignUpUiState()
}