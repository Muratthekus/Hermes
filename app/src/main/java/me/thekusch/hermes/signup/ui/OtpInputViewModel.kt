package me.thekusch.hermes.signup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.thekusch.hermes.supabase.Supabase
import javax.inject.Inject

@HiltViewModel
class OtpInputViewModel @Inject constructor(
    private val supabase: Supabase
) : ViewModel() {


    private val _otpUiState: MutableStateFlow<OtpInputUiState> =
        MutableStateFlow(OtpInputUiState.Init)

    val otpUiState: StateFlow<OtpInputUiState> = _otpUiState

    fun verifyOtp(
        email: String,
        otp: String
    ) {
        viewModelScope.launch {
            _otpUiState.value = OtpInputUiState.Loading
            _otpUiState.value = if (supabase.verifyEmailOtp(email, otp))
                OtpInputUiState.Success
            else OtpInputUiState.Error

        }
    }
}