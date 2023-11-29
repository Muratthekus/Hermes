package me.thekusch.hermes.signup.ui.otp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.R
import me.thekusch.hermes.signup.ui.info.UserInfo
import me.thekusch.hermes.ui.theme.HermesTheme
import me.thekusch.hermes.core.widget.OtpTextField
import me.thekusch.hermes.core.widget.Timer
import me.thekusch.hermes.core.widget.getFieldIconTint
import me.thekusch.hermes.signup.ui.info.SignUpUiState
import me.thekusch.hermes.ui.theme.Error

@AndroidEntryPoint
class OtpInputScreen : Fragment() {

    private lateinit var composeView: ComposeView

    private lateinit var email: String

    private val viewModel by viewModels<OtpInputViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        email = arguments?.getString(OTP_SCREEN_ARGUMENT, "").toString()
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            HermesTheme {
                OtpInputContent()
            }
        }
    }

    @Composable
    fun OtpInputContent() {

        var otpText by remember {
            mutableStateOf("")
        }
        var otpInputFinished by remember {
            mutableStateOf(false)
        }

        val uiState by viewModel.otpUiState.collectAsStateWithLifecycle()

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
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp)
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center,
            ) {

                if (uiState == OtpInputUiState.Loading) {
                    Box(Modifier.fillMaxSize().zIndex(1f)) {
                        CircularProgressIndicator(
                            Modifier.align(Alignment.Center)
                        )
                    }
                }

                if (uiState == OtpInputUiState.Success) {
                    
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (uiState == OtpInputUiState.Loading) 0.3f else 1f)
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {


                    Text(
                        text = stringResource(id = R.string.otp_fragment_title),
                        style = MaterialTheme.typography.h2,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 40.dp, end = 40.dp),
                        text =
                        stringResource(
                            id = R.string.otp_fragment_subtitle,
                            email
                        ),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground
                    )

                    OtpTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        isEnabled = otpInputFinished.not(),
                        otpText = otpText,
                        onOtpTextChange = { value, otpInputFilled ->
                            otpInputFinished = otpInputFilled
                            otpText = value
                            if (otpInputFilled) {
                                viewModel.verifyOtp(email, otpText)
                            }
                        }
                    )
                }

                Timer(
                    modifier = Modifier
                        .padding(bottom = 40.dp, start = 40.dp, end = 40.dp)
                        .alpha(if (uiState == OtpInputUiState.Loading) 0.3f else 1f)
                        .align(Alignment.BottomCenter),
                    timeInMillis = 5 * 1000,
                    onFinishedText = stringResource(id = R.string.otp_fragment_resend_title)
                )

                if (uiState is OtpInputUiState.Error) {
                    ProcessErrorState(uiState = uiState as OtpInputUiState.Error)
                }

            }

        }
    }

    @Composable
    private fun ProcessErrorState(uiState: OtpInputUiState.Error) {
        val text =
            if (uiState.message.isNullOrEmpty())
                stringResource(id = R.string.otp_fragment_error_occurred)
            else uiState.message
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = Error
        )
    }

    companion object {
        private const val OTP_SCREEN_ARGUMENT = "OTP_SCREEN_ARGUMENT"

        @JvmStatic
        fun newInstance(
            email: String
        ) = OtpInputScreen().apply {
            arguments = bundleOf(OTP_SCREEN_ARGUMENT to email)
        }
    }
}