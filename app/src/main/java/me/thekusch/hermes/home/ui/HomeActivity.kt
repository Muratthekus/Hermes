package me.thekusch.hermes.home.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.R
import me.thekusch.hermes.core.base.BaseActivity
import me.thekusch.hermes.intro.ui.IntroFragment

@AndroidEntryPoint
class HomeActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        navigator.apply {
            createNavigatorConfigs(
                supportFragmentManager,
                R.id.container,
                listOf(HomeScreen.newInstance())
            )
            initializeNavigator(savedInstanceState)
        }
    }

    companion object {
        fun newIntent(context: Context) =
            Intent(context, HomeActivity::class.java)
    }
}