package me.thekusch.hermes.login.ui

import me.thekusch.hermes.signup.ui.otp.OtpInputUiState

sealed class LoginUiState {

    object Init: LoginUiState()

    object Success: LoginUiState()

    data class Error(val message: String? = null): LoginUiState()

    object Loading: LoginUiState()
}