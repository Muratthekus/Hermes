package me.thekusch.hermes.intro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.R
import me.thekusch.hermes.ui.theme.HermesTheme
import me.thekusch.hermes.util.widget.HorizontalPager
import me.thekusch.hermes.signup.ui.info.SignUpInfoScreen

@ExperimentalPagerApi
@AndroidEntryPoint
class IntroFragment : Fragment() {

    private lateinit var composeView: ComposeView

    private val viewModel by viewModels<IntroViewModel>()

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
                IntroScreen()
            }
        }
    }

    @Composable
    fun IntroScreen() {
        val introUiState by viewModel.introUiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.getIntroState()
        }
        IntroContent(uiState = introUiState)
    }

    @Composable
    fun IntroContent(
        uiState: IntroUiState
    ) {

        var introPagerIndex by remember {
            mutableStateOf(0)
        }

        if (uiState is IntroUiState.Loading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
            return
        }

        val result = uiState as IntroUiState.Result
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 60.dp)
                    .align(Alignment.Center),
                itemsCount = result.introItems.size,
                items = result.introItems
            ) { introItem, pagerIndex ->
                introPagerIndex = pagerIndex
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(270.dp),
                        painter = painterResource(id = introItem.image),
                        contentDescription = "image"
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(top = 40.dp, start = 45.dp, end = 45.dp),
                        text = stringResource(id = introItem.text),
                        style = MaterialTheme.typography.h2,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = introPagerIndex == result.introItems.size - 1,
            ) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    onClick = {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.container, SignUpInfoScreen.newInstance())
                            ?.addToBackStack(null)
                            ?.commit();
                    }) {
                    Text(
                        text = stringResource(id = R.string.intro_page_button),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = IntroFragment()
    }
}