package com.dinesh.demoforinvoy.viewmodel.intro

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.StringUtils
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.core.preferences.UserPersistence
import com.dinesh.demoforinvoy.core.scheduler.SchedulerProvider
import com.dinesh.demoforinvoy.repositories.AccountRepository
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class IntroViewModel @Inject constructor(
    private val userPersistence: UserPersistence,
    private val schedulerProvider: SchedulerProvider,
    private val accountRepository: AccountRepository,
    private val stringUtils: StringUtils
): BaseViewModel() {

    private val userSignInSuccessTrigger = MutableLiveData<LiveDataResponse<String>>()
    private val introTrigger = MutableLiveData<LiveDataResponse<Boolean>>()

    private val signInIntent = MutableLiveData<LiveDataResponse<Intent>>()
    private val navigationTrigger = MutableLiveData<LiveDataResponse<Boolean>>()

    init {
        schedulerProvider.computation().scheduleDirect(
            { checkForLoggedInUser() },
            2000,
            TimeUnit.MILLISECONDS
        )
    }

    private fun checkForLoggedInUser() {
        userPersistence.getStringOnNull(UserPersistence.KEY_USER_ID)?.let {
            validateUserSignIn(it)
        } ?: triggerIntroFlow()
    }

    private fun validateUserSignIn(userId: String) {

        val observable = accountRepository.validateUserSignIn(userId)

        var observer: Observer<LiveDataResponse<String>>? = null

        observer = Observer {

            when {
                it.isLoading -> Unit
                it.errorMessage != null -> triggerIntroFlow()
                it.data != null -> when(it.data) {
                    null -> triggerIntroFlow()
                    else -> triggerUserValidated(stringUtils.getString(R.string.auto_sign_in_messsage, it.data))
                }
            }
            observer?.let { observer -> observable.removeObserver(observer) }
        }
        schedulerProvider.ui().scheduleDirect {
            observable.observeForever(observer)
        }
    }

    private fun triggerUserValidated(welcomeMessage: String) {
        userSignInSuccessTrigger.postValue(
            LiveDataResponse(
                data = welcomeMessage,
                isLoading = false
            )
        )

        schedulerProvider.io().scheduleDirect(
            { navigationTrigger.postValue(LiveDataResponse(data = true, isLoading = false)) },
            2000,
            TimeUnit.MILLISECONDS
        )
    }

    private fun triggerIntroFlow() {
        introTrigger.postValue(
            LiveDataResponse(
                data = true,
                isLoading = false
            )
        )
    }

    fun signInClicked(context: Context) {
        signInIntent.postValue(LiveDataResponse(data = accountRepository.getSignInIntent(context), isLoading = false))
    }

    fun receivedSignInResult(data: Intent?) {

        val observable = accountRepository.handleSignInIntent(data)
        var observer: Observer<LiveDataResponse<String>>? = null

        observer = Observer {

            when {
                it.isLoading -> Unit
                it.errorMessage != null -> userSignInSuccessTrigger.postValue(it)
                it.data != null -> when(it.data) {
                    null -> Unit
                    else -> triggerUserValidated(stringUtils.getString(R.string.auto_sign_in_messsage, it.data))
                }
            }
            observer?.let { observer -> observable.removeObserver(observer) }
        }
        observable.observeForever(observer)
    }

    fun getUserSignInSuccessTrigger(): LiveData<LiveDataResponse<String>> = userSignInSuccessTrigger
    fun getIntroTrigger(): LiveData<LiveDataResponse<Boolean>> = introTrigger
    fun getSignInIntentTrigger(): LiveData<LiveDataResponse<Intent>> = signInIntent
    fun getNavigationTrigger(): LiveData<LiveDataResponse<Boolean>> = navigationTrigger

    override fun clearReferences() = Unit
}