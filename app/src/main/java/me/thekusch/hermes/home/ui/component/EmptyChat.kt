package me.thekusch.hermes.home.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.thekusch.hermes.R

typealias ComposableView = @Composable () -> Unit

@Composable
private fun EmptyViewSkeleton(
    header: ComposableView,
    content: ComposableView
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        header()
        content()
    }
}

@Composable
fun EmptyChat() {
    EmptyViewSkeleton(header = {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter =
            painterResource(id = R.drawable.example_scene_2_1),
            contentDescription = "example_screne"
        )
    }) {
        Text(
            text = "There is no connection yet",
            style = MaterialTheme.typography.subtitle2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        )
    }
}