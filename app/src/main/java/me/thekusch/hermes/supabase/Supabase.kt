package me.thekusch.hermes.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import javax.inject.Inject

class Supabase @Inject constructor(
    private val supabase: SupabaseClient
) {

    private val goTrue: GoTrue
        get() {
            return supabase.gotrue
        }

    suspend fun signupUser(
        email: String = "vasviye55@gmail.com",
        password: String = "maviDino@512"
    ) {
        goTrue?.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun verifyEmailOtp(
        email: String = "vasviye55@gmail.com",
        token: String = "885049"
    ): Boolean {
        return try {
            goTrue?.verifyEmailOtp(
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