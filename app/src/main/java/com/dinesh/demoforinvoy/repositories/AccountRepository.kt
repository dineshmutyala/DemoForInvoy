package com.dinesh.demoforinvoy.repositories

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dinesh.demoforinvoy.core.firebase.AccountManager
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.datamodels.user.User
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountManager: AccountManager
) {
    fun validateUserSignIn(userId: String): LiveData<LiveDataResponse<User>> {
        return MutableLiveData<LiveDataResponse<User>>().also { observable ->
            accountManager.validateUserSignIn(
                userId,
                { observable.postValue(LiveDataResponse(it, isLoading = false)) },
                { observable.postValue(LiveDataResponse(isLoading = false)) }
            )
        }
    }

    fun handleSignInIntent(data: Intent?): LiveData<LiveDataResponse<User>> {
        return MutableLiveData<LiveDataResponse<User>>().also { observable ->
            accountManager.receivedSignInResult(
                data,
                { observable.postValue(LiveDataResponse(it, isLoading = false)) },
                { observable.postValue(LiveDataResponse(errorMessage = "Could not sign you in! Please try again", isLoading = false)) }
            )
        }
    }

    fun getSignInIntent(context: Context) = accountManager.getSignInIntent(context)

    fun signInWithEmail(email: String, password: String): LiveData<LiveDataResponse<User>> {
        return MutableLiveData<LiveDataResponse<User>>().also { observable ->
            accountManager.signInWithEmail(
                email,
                password,
                { observable.postValue(LiveDataResponse(it, isLoading = false)) },
                { observable.postValue(LiveDataResponse(errorMessage = "Could not sign you in! Please try again", isLoading = false)) }
            )
        }
    }

    fun signOut() {
        accountManager.logOut()
    }
}