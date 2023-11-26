package me.thekusch.hermes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.core.datasource.local.HermesLocalDataSource
import me.thekusch.hermes.intro.ui.IntroFragment

@OptIn(ExperimentalPagerApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, getRootFragment())
            .commit()
    }

    private fun getRootFragment(): Fragment {
        return if (HermesLocalDataSource.isSignUpProcessFinished) {
            BlankFragment.newInstance()
        } else {
            IntroFragment.newInstance()
        }
    }
}