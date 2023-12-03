package me.thekusch.hermes.signup.ui.otp

sealed class OtpInputUiState {

    object Init: OtpInputUiState()

    object Success: OtpInputUiState()

    data class Error(val message: String? = null): OtpInputUiState()

    object Loading: OtpInputUiState()
}