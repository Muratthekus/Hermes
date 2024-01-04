package me.thekusch.hermes

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.thekusch.hermes.home.ui.HomeScreen
import me.thekusch.hermes.intro.ui.IntroFragment

@OptIn(ExperimentalPagerApi::class)
@AndroidEntryPoint
class StarterActivity : AppCompatActivity() {

    private val viewModel by viewModels<StarterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                return@setKeepOnScreenCondition viewModel.isUserLoggedIn.value == null
            }
        }
        setContentView(R.layout.activity_starter)
        setupViewModel()
    }

    private fun setupViewModel() {
        with(viewModel) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    checkIfUserLoggedIn()
                    isUserLoggedIn.collect { loggedIn ->
                        if (loggedIn == true) {
                            supportFragmentManager
                                .beginTransaction()
                                .add(R.id.container, HomeScreen.newInstance())
                                .commit()
                        }
                        if (loggedIn == false) {
                            supportFragmentManager
                                .beginTransaction()
                                .add(R.id.container, IntroFragment.newInstance())
                                .commit()
                        }
                    }
                }
            }
        }
    }

}