package me.thekusch.hermes.home.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import me.thekusch.hermes.R

@AndroidEntryPoint
class HomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, HomeScreen.newInstance())
            .commit()
    }
}