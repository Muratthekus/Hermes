package me.thekusch.hermes.signup.domain

import me.thekusch.hermes.core.domain.SessionManager
import javax.inject.Inject

class OtpUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    suspend fun verifySignUp(email: String, otp: String) =
        sessionManager.verifySignUp(email, otp)

    suspend fun resendOtp(email: String) = sessionManager.resendOtp(email)
}