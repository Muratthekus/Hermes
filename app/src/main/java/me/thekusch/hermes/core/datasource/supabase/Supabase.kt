package me.thekusch.hermes.core.datasource.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.SessionManager
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class Supabase @Inject constructor(
    private val supabase: SupabaseClient
) {

    private val goTrue: GoTrue
        get() {
            return supabase.gotrue
        }

    val sessionStatus: StateFlow<SessionStatus>
        get() {
            return goTrue.sessionStatus
        }

    suspend fun signupUser(
        email: String,
        password: String
    ): String? {
        val result = goTrue.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        return result?.email
    }

    suspend fun signIn(
        email: String,
        password: String
    ) {
        goTrue.loginWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {
        goTrue.logout()
    }

    suspend fun verifyEmailOtp(
        email: String,
        token: String
    ): Boolean {
        Log.d("HERMES","$email -- $token")
        return try {
            goTrue.verifyEmailOtp(
                type = OtpType.Email.SIGNUP,
                email = email,
                token = token
            )
            true
        } catch (exception: Exception) {
            Log.d("HERMES", exception.localizedMessage?.toString() ?: "error")
            false
        }
    }

    suspend fun resendOtp(
        email: String
    ) {
        goTrue.resendEmail(OtpType.Email.SIGNUP, email)
    }
}