package me.thekusch.hermes.core.util

import androidx.compose.ui.graphics.Color
import java.util.Random

fun getRandomColor(): Color {
    val rnd = Random()
    return Color(255,rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}