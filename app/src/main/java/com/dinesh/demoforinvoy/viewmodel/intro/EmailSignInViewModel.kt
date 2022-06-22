package com.dinesh.demoforinvoy.viewmodel.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.StringUtils
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.core.scheduler.SchedulerProvider
import com.dinesh.demoforinvoy.datamodels.user.User
import com.dinesh.demoforinvoy.repositories.AccountRepository
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EmailSignInViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val stringUtils: StringUtils,
    private val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    private val userSignInSuccessTrigger = MutableLiveData<LiveDataResponse<String>>()
    private val navigationTrigger = MutableLiveData<LiveDataResponse<Boolean>>()

    fun signInClicked(email: String, password: String) {
        val observable = accountRepository.signInWithEmail(email, password)
        var observer: Observer<LiveDataResponse<User>>? = null

        observer = Observer {

            when {
                it.isLoading -> Unit
                it.errorMessage != null -> Unit
                it.data != null -> when(it.data) {
                    null -> Unit
                    else -> triggerUserValidated(stringUtils.getString(R.string.auto_sign_in_messsage, it.data.name), it.data.isACoach)
                }
            }
            observer?.let { observer -> observable.removeObserver(observer) }
        }
        observable.observeForever(observer)
    }

    private fun triggerUserValidated(welcomeMessage: String, isACoach: Boolean) {
        userSignInSuccessTrigger.postValue(
            LiveDataResponse(
                data = welcomeMessage,
                isLoading = false
            )
        )

        schedulerProvider.io().scheduleDirect(
            { navigationTrigger.postValue(LiveDataResponse(data = isACoach, isLoading = false)) },
            2000,
            TimeUnit.MILLISECONDS
        )
    }

    fun getUserSignInSuccessTrigger(): LiveData<LiveDataResponse<String>> = userSignInSuccessTrigger
    fun getNavigationTrigger(): LiveData<LiveDataResponse<Boolean>> = navigationTrigger

    override fun clearReferences() {

    }
}