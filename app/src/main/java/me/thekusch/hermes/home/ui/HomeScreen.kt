package me.thekusch.hermes.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import me.thekusch.hermes.ui.theme.Error
import me.thekusch.hermes.ui.theme.HermesTheme

class HomeScreen : Fragment() {

    private lateinit var composeView: ComposeView

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
        composeView.setContent {
            HermesTheme {
                HomeContent()
            }
        }
    }

    @Composable
    private fun HomeContent() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeScreen()
    }
}