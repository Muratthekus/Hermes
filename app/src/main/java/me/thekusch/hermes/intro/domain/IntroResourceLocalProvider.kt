package me.thekusch.hermes.intro.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import me.thekusch.hermes.R
import javax.inject.Inject

class IntroResourceLocalProvider @Inject constructor() {

    fun provideIntroItems(): List<IntroItem> = buildList {
        add(
            IntroItem(
                R.drawable.intro_item_1,
                R.string.intro_item_1_text
            )
        )
        add(
            IntroItem(
                R.drawable.intro_item_1,
                R.string.intro_item_2_text
            )
        )
        add(
            IntroItem(
                R.drawable.intro_item_1,
                R.string.intro_item_3_text
            )
        )
        add(
            IntroItem(
                R.drawable.intro_item_1,
                R.string.intro_item_4_text
            )
        )
    }
}


data class IntroItem(
    @DrawableRes val image: Int,
    @StringRes val text: Int
)