package me.thekusch.hermes.login.ui

import android.os.Bundle
import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import me.thekusch.hermes.signup.ui.info.UserInfo


class LoginScreenContentState(val userInfo: UserInfo) {

    private var _email by mutableStateOf(userInfo.email)

    private var _password by mutableStateOf(userInfo.password)

    private var _isPasswordVisible by mutableStateOf(false)

    var email: String
        get() = _email
        set(value) {
            _email = value
        }

    var isPasswordVisible: Boolean
        get() = _isPasswordVisible
        set(value) {
            _isPasswordVisible = value
        }

    var password: String
        get() = _password
        set(value) {
            _password = value
        }


    private val isValidEmail by derivedStateOf { isValidEmail(_email) }

    val isPasswordLegit by derivedStateOf { _password.length > 6 }

    private fun isValidEmail(email: String): Boolean =
        email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isButtonEnabled: Boolean
        get() {
            return isValidEmail &&
                    isPasswordLegit
        }

    companion object {

        fun signUpStateSaver(): Saver<LoginScreenContentState, *> {
            return Saver(
                save = { state ->
                    Bundle().apply {
                        putParcelable(
                            "loginKey", state.userInfo
                        )
                    }
                },
                restore = {
                    val savedState = it.getParcelable<UserInfo>("loginKey")
                    LoginScreenContentState(savedState as UserInfo)
                }
            )
        }
    }
}

@Composable
fun rememberLoginContentState(
    email: String = "",
    password: String = "",
): LoginScreenContentState = rememberSaveable(
    saver = LoginScreenContentState.signUpStateSaver()
) {
    LoginScreenContentState(UserInfo(email, password))
}