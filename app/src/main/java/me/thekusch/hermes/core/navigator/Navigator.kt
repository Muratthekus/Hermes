package me.thekusch.hermes.core.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.trendyol.medusalib.navigator.MultipleStackNavigator
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.NavigatorConfiguration
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import javax.inject.Inject
import javax.inject.Qualifier

@Qualifier
@Retention
annotation class DefaultNavigatorQualifier

@Qualifier
@Retention
annotation class DefaultFragmentNavigatorQualifier

interface HermesNavigator : Navigator.NavigatorListener {

    fun createNavigatorConfigs(
        fragmentManager: FragmentManager,
        containerId: Int,
        rootFragmentProvider: List<Fragment>,
    )

    fun initializeNavigator(
        savedInstance: Bundle?
    )

    fun isNavigatorInitialized(): Boolean

    fun startFragment(fragment: Fragment)

    fun startFragment(fragment: Fragment, groupName: String)

    fun goBack()

    fun clearGroup(groupName: String)

    fun backPressed(onBackPressed: () -> Unit)
}

class HermesNavigatorImpl @Inject constructor() : HermesNavigator {

    private lateinit var navigator: Navigator


    override fun initializeNavigator(savedInstance: Bundle?) {
        this.navigator.initialize(savedInstance)
    }

    override fun createNavigatorConfigs(
        fragmentManager: FragmentManager,
        containerId: Int,
        rootFragmentProvider: List<Fragment>,
    ) {
        this.navigator = MultipleStackNavigator(
            fragmentManager = fragmentManager,
            containerId = containerId,
            rootFragmentProvider = rootFragmentProvider.map { { it } },
            navigatorConfiguration = NavigatorConfiguration(
                initialTabIndex = 0,
                alwaysExitFromInitial = true,
                defaultNavigatorTransaction = NavigatorTransaction.SHOW_HIDE
            )
        )
    }


    /**
     * @return true if navigator is initialized
     */
    override fun isNavigatorInitialized(): Boolean {
        return this::navigator.isInitialized
    }

    /**
     * Start fragment
     * @param fragment fragment to start
     */
    override fun startFragment(fragment: Fragment) {
        if (isNavigatorInitialized()) {
            navigator.start(fragment)
        }
    }


    override fun startFragment(fragment: Fragment, groupName: String) {
        navigator.start(fragment, groupName)

    }

    override fun backPressed(onBackPressed: () -> Unit) {
        if (isNavigatorInitialized().not()) {
            onBackPressed()
            return
        }
        if (navigator.canGoBack()) {
            navigator.goBack()
        } else {
            onBackPressed()
        }
    }

    override fun onTabChanged(tabIndex: Int) {

    }

    override fun clearGroup(groupName: String) {
        navigator.clearGroup(groupName)
    }

    override fun goBack() {
        navigator.goBack()
    }
}

interface HermesFragmentNavigator {

    fun startFragment(fragment: Fragment)

    fun startFragment(fragment: Fragment, groupName: String)

    fun getNavigatorFromActivity(
        navigator: HermesNavigator
    )

    fun goBack()

    fun clearGroup(groupName: String)
}

class HermesFragmentNavigatorImpl @Inject constructor() : HermesFragmentNavigator {

    private lateinit var fragmentNavigator: HermesNavigator

    private fun canNavigate(): Boolean {
        return this::fragmentNavigator.isInitialized
    }

    override fun getNavigatorFromActivity(navigator: HermesNavigator) {
        fragmentNavigator = navigator
    }

    override fun startFragment(fragment: Fragment) {
        if (canNavigate()) {
            fragmentNavigator.startFragment(fragment)
        }
    }


    override fun startFragment(fragment: Fragment, groupName: String) {
        if (canNavigate()) {
            fragmentNavigator.startFragment(fragment, groupName)
        }
    }

    override fun goBack() {
        if (canNavigate()) {
            fragmentNavigator.goBack()
        }
    }

    override fun clearGroup(groupName: String) {
        fragmentNavigator.clearGroup(groupName)
    }
}