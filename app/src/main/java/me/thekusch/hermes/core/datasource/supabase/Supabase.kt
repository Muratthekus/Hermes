package me.thekusch.hermes.core.datasource.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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

    @Throws
    suspend fun signupUser(
        email: String,
        password: String,
        name: String
    ) {
        val result = runCatching {
            goTrue.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("userValidName",name)
                }
            }
        }
        result.getOrThrow()
    }

    @Throws
    suspend fun resendOtp(
        email: String
    ) {
        val result = runCatching {
            goTrue.resendEmail(OtpType.Email.SIGNUP, email)
        }
        result.getOrThrow()
    }

    suspend fun signIn(
        email: String,
        password: String
    ): Boolean {
        return try {
            goTrue.loginWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun logout() {
        goTrue.logout()
    }

    suspend fun verifyEmailOtp(
        email: String,
        token: String
    ): Boolean {
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
}