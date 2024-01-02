package me.thekusch.hermes.signup.ui.info

import android.os.Bundle
import android.os.Parcelable
import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
class UserInfo(
    val email: String = "",
    val password: String = "",
    val name: String = "",
) : Parcelable

class SignUpContentState(val userInfo: UserInfo) {

    private var _email by mutableStateOf(userInfo.email)

    private var _password by mutableStateOf(userInfo.password)

    private var _name by mutableStateOf(userInfo.name)

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

    var name: String
        get() = _name
        set(value) {
            _name = value
        }

    private val isValidEmail by derivedStateOf { isValidEmail(_email) }

    val isPasswordLegit by derivedStateOf { _password.length > 6 }

    private fun isValidEmail(email: String): Boolean =
        email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val isButtonEnabled: Boolean
        get() {
            return isValidEmail &&
                    isPasswordLegit && name.isNotEmpty()
        }

    companion object {

        fun signUpStateSaver(): Saver<SignUpContentState, *> {
            return Saver(
                save = { state ->
                    Bundle().apply {
                        putParcelable(
                            "signUpKey", state.userInfo
                        )
                    }
                },
                restore = {
                    val savedState = it.getParcelable<UserInfo>("signUpKey")
                    SignUpContentState(savedState as UserInfo)
                }
            )
        }
    }
}

@Composable
fun rememberSignUpContentState(
    email: String = "",
    password: String = "",
    name: String = "",
): SignUpContentState = rememberSaveable(
    saver = SignUpContentState.signUpStateSaver()
) {
    SignUpContentState(UserInfo(email, password, name))
}