package me.thekusch.hermes.core.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import me.thekusch.hermes.core.navigator.DefaultNavigatorQualifier
import me.thekusch.hermes.core.navigator.HermesNavigator
import javax.inject.Inject

abstract class BaseActivity: AppCompatActivity() {

    @Inject
    @DefaultNavigatorQualifier
    lateinit var navigator: HermesNavigator

    /**
     * Children of BaseActivity must override this method if they want to handle back press
     * */
    open var onBackPressedListener: () -> Unit = {
        finish()
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    private fun backPressed() {
        navigator.backPressed {
            this.onBackPressedListener()
        }
    }
}