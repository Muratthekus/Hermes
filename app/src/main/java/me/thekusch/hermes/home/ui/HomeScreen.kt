package me.thekusch.hermes.home.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.thekusch.hermes.R
import me.thekusch.hermes.core.widget.getFieldIconTint
import me.thekusch.hermes.home.ui.component.CreateChat
import me.thekusch.hermes.home.ui.component.CreateChatDialog
import me.thekusch.hermes.home.ui.component.EmptyChat
import me.thekusch.hermes.home.ui.component.UserChatHistoryList
import me.thekusch.hermes.ui.theme.HermesTheme
import me.thekusch.hermes.ui.theme.LightGray
import me.thekusch.messager.Hermes

@AndroidEntryPoint
class HomeScreen : Fragment() {

    private lateinit var composeView: ComposeView

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initHermes(requireActivity())
        composeView.setContent {
            HermesTheme {
                HomeContent()
            }
        }
    }

    @Composable
    fun HomeContent() {

        val (showCreateConnection, setShowCreateConnection) =
            remember { mutableStateOf(false) }
        val listState = rememberLazyListState()

        val homeState by viewModel.homeState.collectAsStateWithLifecycle()
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(key1 = Unit, block = {
            viewModel.getChatHistory()
        })

        Scaffold(
            modifier = Modifier.background(MaterialTheme.colors.background),
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chats",
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onBackground,
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = { setShowCreateConnection(true) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.create_chat),
                                contentDescription = "create chat",
                                tint = getFieldIconTint()
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = LightGray)
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {

                if (homeState.uiState.isLoading())
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }

                if (homeState.uiState.isEmptyChat())
                    EmptyChat()

                if (homeState.uiState.isCreateChat()) {
                    val data = homeState.uiState as HomeUiState.CreateChat
                    CreateChat(
                        selectedMethod = data.selectedCreateChatMethod,
                        hermesState = homeState.hermesState
                    ) {
                        viewModel.dismissCreateChat()
                    }
                }

                if (homeState.uiState.isSuccess()) {
                    val data = homeState.uiState as HomeUiState.Success
                    UserChatHistoryList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        chatList = data.chatHistory,
                        state = listState
                    ) { clickedChat ->

                    }
                }

                if (showCreateConnection) {
                    CreateChatDialog(
                        onDismiss = { setShowCreateConnection(false) }
                    ) {
                        viewModel.createChat(it)
                    }
                }

                CheckPermission(homeState.permissionUiState, scope, snackbarHostState)
                GetError(homeState.errorState, scope, snackbarHostState)

            }
        }
    }

    @Composable
    private fun GetError(
        errorState: String,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState
    ) {
        LaunchedEffect(key1 = errorState) {
            if (errorState.isEmpty())
                return@LaunchedEffect

            scope.launch {
                snackbarHostState
                    .showSnackbar(
                        message = errorState,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
            }
        }
    }

    @Composable
    private fun CheckPermission(
        permissionUiState: Boolean,
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState
    ) {
        LaunchedEffect(key1 = permissionUiState) {
            if (permissionUiState)
                return@LaunchedEffect

            scope.launch {
                val result = snackbarHostState
                    .showSnackbar(
                        message = "All permissions should be granted",
                        actionLabel = "Settings",
                        // Defaults to SnackbarDuration.Short
                        duration = SnackbarDuration.Indefinite
                    )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri =
                            Uri.fromParts("package", requireContext().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }

                    SnackbarResult.Dismissed -> {
                        /* Handle snackbar dismissed */
                    }

                    else -> {}
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeScreen()
    }
}