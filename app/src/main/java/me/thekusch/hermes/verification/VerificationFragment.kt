package me.thekusch.hermes.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.R
import me.thekusch.hermes.ui.theme.HermesTheme

@AndroidEntryPoint
class VerificationFragment : Fragment() {

    private lateinit var composeView: ComposeView

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
                VerificationContent()
            }
        }
    }

    @Composable
    fun VerificationScreen() {

    }

    @Composable
    fun VerificationContent() {

        var emailAddress by remember {
            mutableStateOf("")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.verification_fragment_title),
                style = MaterialTheme.typography.h2,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground
            )

            Text(
                modifier = Modifier.padding(top = 8.dp, start = 40.dp, end = 40.dp),
                text = stringResource(id = R.string.verification_fragment_subtitle),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onBackground
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(horizontal = 24.dp),
                value = emailAddress,
                onValueChange = { emailAddress = it },
                placeholder = { stringResource(id = R.string.verification_fragment_email_hint) },
                textStyle = MaterialTheme.typography.body1,
                shape = RoundedCornerShape(8.dp),
                label = { "asfgasg" }
            )
        }
    }

    @Preview
    @Composable
    fun VerificationPreview() {
        VerificationContent()
    }

    companion object {
        @JvmStatic
        fun newInstance() = VerificationFragment()
    }

}
