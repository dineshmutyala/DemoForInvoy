package com.dinesh.demoforinvoy.core.firebase

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.preferences.UserPersistence
import com.dinesh.demoforinvoy.datamodels.user.User
import com.dinesh.demoforinvoy.di.AppContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManager @Inject constructor(private val userPersistence: UserPersistence) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val userData: MutableLiveData<User> = MutableLiveData<User>()
    private var currentUser: User? = null

    fun getSignInIntent(@AppContext context: Context): Intent {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        return GoogleSignIn.getClient(context, options).signInIntent
    }

    fun receivedSignInResult(data: Intent?, successListener: (String) -> Unit, failureListener: (Exception) -> Unit) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        googleAuthForFirebase(it.result.idToken, successListener, failureListener)
                        it.result.idToken?.let {
                            idToken -> userPersistence.addToPersistence(UserPersistence.KEY_USER_ID, idToken)
                        }
                    }
                    false -> {
                        (it.exception ?: Exception()).also { e -> failureListener.invoke(e) }
                    }
            }
            }
    }

    private fun googleAuthForFirebase(
        idToken: String?,
        successListener: (String) -> Unit,
        failureListener: (Exception) -> Unit
    ) {
        try {
            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnSuccessListener {
                    it.user?.let { user ->
                        successListener.invoke(user.displayName ?: "")
                        User(userId = user.uid, name = user.displayName ?: "").also { userObj ->
                            currentUser = userObj
                            userData.postValue(userObj)
                        }
                    }
                }
                .addOnFailureListener { failureListener.invoke(it) }
        } catch (e: Exception) {
            failureListener.invoke(e)
        }
    }

    fun validateUserSignIn(
        userId: String,
        successListener: (String) -> Unit,
        failureListener: (Exception) -> Unit
    ) {
        googleAuthForFirebase(userId, successListener, failureListener)
    }

    fun signInWithEmail(email: String, password: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    task.result.user?.let { user ->
                        onSuccess.invoke(user.displayName ?: "")
                        User(userId = user.uid, name = user.displayName ?: "").also { userObj ->
                            currentUser = userObj
                            userData.postValue(userObj)
                        }
                    }
                }
                else -> {
                    onFailure.invoke(task.exception ?: Exception())
                }
            }
        }
    }

    fun logOut() {
        auth.signOut()
        userPersistence.removeFromPersistence(UserPersistence.KEY_USER_ID)
        userData.postValue(User.UNKNOWN_USER)
    }

    fun observeUserData(): LiveData<User> = userData
    fun getCurrentUser(): User? = currentUser
}