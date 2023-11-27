package me.thekusch.hermes.signup.ui.register

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.BlankFragment
import me.thekusch.hermes.R
import me.thekusch.hermes.signup.ui.otp.OtpInputScreen
import me.thekusch.hermes.ui.theme.Black
import me.thekusch.hermes.ui.theme.HermesTheme
import me.thekusch.hermes.ui.theme.WhiteVariant
import me.thekusch.hermes.util.widget.getFieldIconTint
import me.thekusch.hermes.util.widget.provideTextFieldColors

@Deprecated("Register flow is updated, remove RegisterScreen from register flow")
@AndroidEntryPoint
class RegisterScreen : Fragment() {

    private lateinit var composeView: ComposeView

    private val viewModel by viewModels<RegisterViewModel>()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                pickMediaResult(uri)
            }
        }

    lateinit var pickMediaResult: (Uri) -> Unit

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
                RegisterContent()
            }
        }
    }

    @Composable
    fun RegisterContent() {

        var username by remember {
            mutableStateOf("")
        }

        var mediaUri: Uri by remember {
            mutableStateOf(Uri.EMPTY)
        }

        val finalizeProcess = viewModel.finalizeProcess.collectAsStateWithLifecycle().value

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
            if (finalizeProcess) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, BlankFragment.newInstance())
                    ?.addToBackStack(null)
                    ?.commit()
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(paddingValues)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = stringResource(id = R.string.register_screen_title),
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onBackground
                )

                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(100.dp)
                        .background(
                            color = if (false) Black else WhiteVariant,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        modifier = Modifier
                            .clickable {
                                pickPhoto {
                                    mediaUri = it
                                }
                            }
                            .fillMaxSize()
                            .clip(CircleShape),
                        painter = rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = mediaUri)
                                .build()
                        ),
                        contentScale = ContentScale.Crop,
                        contentDescription = ""
                    )

                    if (mediaUri == Uri.EMPTY) {

                        Icon(
                            modifier = Modifier
                                .clickable {
                                    pickPhoto {
                                        mediaUri = it
                                    }
                                }
                                .align(Alignment.BottomEnd)
                                .padding(end = 4.dp, bottom = 4.dp),
                            painter = painterResource(id = R.drawable.add_icon),
                            contentDescription = "profile icon",
                            tint = getFieldIconTint()
                        )

                    }

                }

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 40.dp)
                        .height(50.dp),
                    value = username,
                    onValueChange = { username = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.register_username_title),
                            style = MaterialTheme.typography.body1,
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = MaterialTheme.typography.body1,
                    colors = provideTextFieldColors(),
                )

                Button(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 60.dp)
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    enabled = username.isNotEmpty(),
                    onClick = {
                        viewModel.finalizeSignUpProcess(username)
                    }) {
                    Text(
                        text = stringResource(id = R.string.register_button),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }

    private fun pickPhoto(result: (Uri) -> Unit) {
        pickMediaResult = result
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterScreen()
    }
}