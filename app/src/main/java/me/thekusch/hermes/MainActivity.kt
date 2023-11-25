package me.thekusch.hermes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.intro.ui.IntroFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, IntroFragment.newInstance())
            .commit()

    }
}