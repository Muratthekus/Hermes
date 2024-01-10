package me.thekusch.hermes.core.base

import androidx.fragment.app.Fragment
import me.thekusch.hermes.core.navigator.DefaultFragmentNavigatorQualifier
import me.thekusch.hermes.core.navigator.HermesFragmentNavigator
import javax.inject.Inject

abstract class BaseFragment: Fragment() {

    @Inject
    @DefaultFragmentNavigatorQualifier
    lateinit var navigator: HermesFragmentNavigator

}