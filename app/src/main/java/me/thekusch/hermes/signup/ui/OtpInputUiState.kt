package me.thekusch.hermes.signup.ui

sealed class OtpInputUiState {

    object Init: OtpInputUiState()

    object Success: OtpInputUiState()

    object Error: OtpInputUiState()

    object Loading: OtpInputUiState()
}