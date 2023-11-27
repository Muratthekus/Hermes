package me.thekusch.hermes.signup.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.R
import me.thekusch.hermes.signup.ui.otp.OtpInputScreen
import me.thekusch.hermes.ui.theme.Error
import me.thekusch.hermes.ui.theme.HermesTheme
import me.thekusch.hermes.util.widget.getFieldIconTint
import me.thekusch.hermes.util.widget.provideTextFieldColors

@AndroidEntryPoint
class SignUpInfoScreen : Fragment() {

    private lateinit var composeView: ComposeView

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            HermesTheme {
                VerificationScreen()
            }
        }
    }

    @Composable
    fun VerificationScreen() {
        VerificationContent()
    }

    @Composable
    fun VerificationContent() {

        val uiState by viewModel.signUpUiState.collectAsStateWithLifecycle()

        val contentState = rememberSignUpContentState()

        Scaffold(
            modifier = Modifier.background(MaterialTheme.colors.background),
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.white_back_arrow),
                            contentDescription = "show password",
                            tint = getFieldIconTint()
                        )
                    }

                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.help_circle_outline),
                            contentDescription = "show password",
                            tint = getFieldIconTint()
                        )
                    }
                }
            }
        ) { paddingValues ->
            if (uiState == SignUpUiState.Loading) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center)
                    )
                }
            }
            if (uiState == SignUpUiState.Success) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(
                        R.id.container,
                        OtpInputScreen.newInstance(contentState.email)
                    )
                    ?.addToBackStack(null)
                    ?.commit()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = stringResource(id = R.string.signup_info_fragment_title),
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 40.dp, end = 40.dp),
                    text = stringResource(id = R.string.signup_info_fragment_subtitle),
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 40.dp)
                        .height(50.dp),
                    value = contentState.name,
                    onValueChange = { contentState.name = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.signup_info_fragment_name_hint),
                            style = MaterialTheme.typography.body1,
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    textStyle = MaterialTheme.typography.body1,
                    colors = provideTextFieldColors(),
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 40.dp)
                        .height(50.dp),
                    value = contentState.email,
                    onValueChange = { contentState.email = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.signup_info_fragment_email_hint),
                            style = MaterialTheme.typography.body1,
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = MaterialTheme.typography.body1,
                    colors = provideTextFieldColors(),
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 20.dp)
                        .height(50.dp),
                    value = contentState.password,
                    onValueChange = { contentState.password = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.signup_info_fragment_password_hint),
                            style = MaterialTheme.typography.body1,
                        )
                    },
                    visualTransformation = if (contentState.isPasswordVisible)
                        VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = MaterialTheme.typography.body1,
                    colors = provideTextFieldColors(),
                    trailingIcon = {
                        val image = if (contentState.isPasswordVisible)
                            painterResource(id = R.drawable.password_show)
                        else painterResource(id = R.drawable.password_hide)

                        val description =
                            if (contentState.isPasswordVisible) "Hide password" else "Show password"

                        IconButton(onClick = {
                            contentState.isPasswordVisible = contentState.isPasswordVisible.not()
                        }) {
                            Icon(
                                painter = image,
                                contentDescription = description,
                                tint = getFieldIconTint()
                            )
                        }
                    },
                    isError = contentState.isPasswordLegit.not(),
                )
                if (contentState.isPasswordLegit.not()) {
                    Text(
                        text = stringResource(id = R.string.signup_info_fragment_password_length_error),
                        color = Error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 4.dp),
                        textAlign = TextAlign.Start
                    )
                }

                Button(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 60.dp)
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    enabled = contentState.isButtonEnabled &&
                            uiState != SignUpUiState.Loading,
                    onClick = {
                        viewModel.signUpUser(
                            contentState.name,
                            contentState.email,
                            contentState.password
                        )
                    }) {
                    Text(
                        text = stringResource(id = R.string.signup_info_fragment_button),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun VerificationPreview() {
        VerificationContent()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpInfoScreen()
    }

}
