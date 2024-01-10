package me.thekusch.hermes

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.thekusch.hermes.core.base.BaseActivity
import me.thekusch.hermes.core.navigator.DefaultNavigatorQualifier
import me.thekusch.hermes.core.navigator.HermesNavigator
import me.thekusch.hermes.home.ui.HomeActivity
import me.thekusch.hermes.home.ui.HomeScreen
import me.thekusch.hermes.intro.ui.IntroFragment
import javax.inject.Inject


@OptIn(ExperimentalPagerApi::class)
@AndroidEntryPoint
class StarterActivity : BaseActivity() {

    private val viewModel by viewModels<StarterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                return@setKeepOnScreenCondition viewModel.isUserLoggedIn.value == null
            }
        }
        navigator.apply {
            createNavigatorConfigs(
                supportFragmentManager,
                R.id.container,
                listOf(IntroFragment.newInstance())
            )
            initializeNavigator(savedInstanceState)
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
                            this@StarterActivity.startActivity(
                                HomeActivity.newIntent(this@StarterActivity)
                            )
                            finish()
                        }
                        if (loggedIn == false) {
                            navigator.startFragment(IntroFragment.newInstance())
                        }
                    }
                }
            }
        }
    }

}