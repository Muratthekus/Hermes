package me.thekusch.hermes.signup.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.thekusch.hermes.core.datasource.supabase.Supabase
import javax.inject.Inject

@HiltViewModel
class OtpInputViewModel @Inject constructor(
    private val supabase: Supabase
) : ViewModel() {

    private lateinit var verifyJob: Job

    private val _otpUiState: MutableStateFlow<OtpInputUiState> =
        MutableStateFlow(OtpInputUiState.Init)

    val otpUiState: StateFlow<OtpInputUiState> = _otpUiState

    fun verifyOtp(
        email: String,
        otp: String
    ) {
        if (::verifyJob.isInitialized)
            verifyJob.cancel()

        verifyJob = viewModelScope.launch {
            _otpUiState.value = OtpInputUiState.Loading
            _otpUiState.value = if (supabase.verifyEmailOtp(email, otp)) {
                OtpInputUiState.Success
            }
            else OtpInputUiState.Error

        }
    }
}