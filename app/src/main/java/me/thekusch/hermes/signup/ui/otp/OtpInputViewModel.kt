package me.thekusch.hermes.signup.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.thekusch.hermes.core.domain.SessionManager
import me.thekusch.hermes.core.datasource.local.model.Result
import me.thekusch.hermes.signup.domain.OtpUseCase
import javax.inject.Inject

// TODO(murat) add OTP usecase, viewmodel should not access SessionManager
@HiltViewModel
class OtpInputViewModel @Inject constructor(
    private val otpUseCase: OtpUseCase
) : ViewModel() {

    private lateinit var verifyJob: Job

    private val _otpUiState: MutableStateFlow<OtpInputUiState> =
        MutableStateFlow(OtpInputUiState.Init)

    val otpUiState: StateFlow<OtpInputUiState> = _otpUiState

    fun verifyOtp(
        email: String,
        otp: String
    ) {
        if (::verifyJob.isInitialized) {
            _otpUiState.value = OtpInputUiState.Init
            verifyJob.cancel()
        }

        verifyJob = viewModelScope.launch {
            otpUseCase.verifySignUp(email, otp).collectLatest {
                when (it) {
                    is Result.Started -> {
                        _otpUiState.value = OtpInputUiState.Loading
                    }

                    is Result.Success -> {
                        _otpUiState.value = OtpInputUiState.Success
                    }

                    is Result.Fail -> {
                        _otpUiState.value = OtpInputUiState.Error(it.exception.localizedMessage)
                    }

                    else -> {
                        _otpUiState.value = OtpInputUiState.Error()
                    }
                }
            }
        }
    }
}