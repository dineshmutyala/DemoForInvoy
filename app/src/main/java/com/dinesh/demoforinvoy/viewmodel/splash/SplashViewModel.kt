package com.dinesh.demoforinvoy.viewmodel.splash

import com.dinesh.demoforinvoy.misc.UserPersistence
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val userPersistence: UserPersistence
): BaseViewModel() {
    override fun clearReferences() = Unit
}