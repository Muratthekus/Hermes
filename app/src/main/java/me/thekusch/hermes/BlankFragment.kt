package me.thekusch.hermes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import me.thekusch.messager.Hermes
import me.thekusch.messager.controller.AdvertiseStatus
import me.thekusch.messager.controller.BaseStatus
import me.thekusch.messager.controller.DiscoveryStatus


class BlankFragment : Fragment() {

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
        val hermes = Hermes(requireActivity(),::onPermissionNotGranted)
        composeView.setContent {
            HomeScreen(hermes = hermes)
        }
    }

    private fun onPermissionNotGranted() {
        val snackbar = Snackbar.make(composeView, "All permissions should be granted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Settings") {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri =
                Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        snackbar.show()
    }

    @Composable
    fun HomeScreen(
        hermes: Hermes
    ) {
        var username by remember {
            mutableStateOf("")
        }
        var isUserNameSaved by remember {
            mutableStateOf(false)
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Hermes")
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    readOnly = isUserNameSaved,
                    placeholder = { "please enter a username" },
                )
                Button(onClick = {
                    hermes.setUserName(username)
                    isUserNameSaved = true
                }, enabled = isUserNameSaved.not()) {

                    val text = if (isUserNameSaved)
                        username
                    else "Save username"
                    Text(text = text)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    Button(enabled = isUserNameSaved,
                        onClick = { hermes.startAdvertising() }) {
                        Text(text = "Start Advertising")
                    }
                    Button(enabled = isUserNameSaved,
                        onClick = { hermes.startDiscovery() }) {
                        Text(text = "Start Discovery")
                    }
                }

                AdvertiseFactory(hermes)

                DiscoveryFactory(hermes)
            }
        }
    }

    @Composable
    fun AdvertiseFactory(
        hermes: Hermes
    ) {
        var advertiseStatus: BaseStatus by remember {
            mutableStateOf(BaseStatus.Initial)
        }
        hermes.advertiseStatusListener = { status ->
            advertiseStatus = status
        }

        when (advertiseStatus) {
            is AdvertiseStatus.FinishedSuccessfully -> {
                Toast.makeText(context, "Advertise started successfully", Toast.LENGTH_SHORT)
                    .show()
            }

            is AdvertiseStatus.FinishedWithError -> {
                Toast.makeText(context, "Advertise failed", Toast.LENGTH_SHORT).show()
                Log.e(
                    "HERMES",
                    (advertiseStatus as AdvertiseStatus.FinishedWithError)
                        .exception.localizedMessage?.toString()
                        ?: "error couldn't fetch"
                )
            }

            is BaseStatus.ConnectionResultStatus -> {
                Toast.makeText(
                    context,
                    (advertiseStatus as BaseStatus.ConnectionResultStatus)
                        .result, Toast.LENGTH_SHORT
                ).show()
            }

            is BaseStatus.Disconnected -> {
                Toast.makeText(context, "Disconnected from endpoint", Toast.LENGTH_SHORT)
                    .show()
            }

            is BaseStatus.ConnectionInitiated -> {
                AlertDialog(
                    hermes = hermes,
                    data = advertiseStatus as BaseStatus.ConnectionInitiated
                )
            }

            else -> {

            }

        }
    }

    @Composable
    fun DiscoveryFactory(
        hermes: Hermes
    ) {
        var discoveryStatus: BaseStatus by remember {
            mutableStateOf(BaseStatus.Initial)
        }
        hermes.discoveryStatusListener = { status ->
            discoveryStatus = status
        }

        when (discoveryStatus) {
            is DiscoveryStatus.DiscoveryStarted -> {
                Toast.makeText(context, "Discovery started successfully", Toast.LENGTH_SHORT)
                    .show()
            }

            is DiscoveryStatus.DiscoveryFailed -> {
                Toast.makeText(context, "Discovery failed", Toast.LENGTH_SHORT).show()
                Log.e(
                    "HERMES",
                    "error couldn't fetch"
                )
            }

            is DiscoveryStatus.EndpointFound -> {
                Toast.makeText(context, "Geldi bi şeyler", Toast.LENGTH_SHORT).show()
                Log.d(
                    "HERMES",
                    "${(discoveryStatus as DiscoveryStatus.EndpointFound).endpointName}"
                )
            }

            is DiscoveryStatus.EndpointLost -> {
                Toast.makeText(
                    context,
                    "Endpoint lost ${(discoveryStatus as DiscoveryStatus.EndpointLost).endpointId}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            is BaseStatus.ConnectionResultStatus -> {
                Toast.makeText(
                    context,
                    (discoveryStatus as BaseStatus.ConnectionResultStatus)
                        .result, Toast.LENGTH_SHORT
                ).show()
            }

            is BaseStatus.Disconnected -> {
                Toast.makeText(context, "Disconnected from endpoint", Toast.LENGTH_SHORT)
                    .show()
            }

            is BaseStatus.ConnectionInitiated -> {
                AlertDialog(
                    hermes = hermes,
                    data = discoveryStatus as BaseStatus.ConnectionInitiated
                )
            }

            else -> {

            }

        }
    }

    @Composable
    fun AlertDialog(
        hermes: Hermes,
        data: BaseStatus.ConnectionInitiated
    ) {
        var openDialog by remember { mutableStateOf(true) }
        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    openDialog = false
                },
                title = {
                    Text(text = "Incoming Connection Request")
                },
                text = {
                    Text("Endpoint Name ${data.endpointName}")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            hermes.acceptConnection(data.endpointId, requireContext())
                            openDialog = false
                        }) {
                        Text("Accept Connection")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            hermes.rejectConnection(data.endpointId, requireContext())
                            openDialog = false
                        }) {
                        Text("Reject Connection")
                    }
                }
            )
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = BlankFragment()
    }
}