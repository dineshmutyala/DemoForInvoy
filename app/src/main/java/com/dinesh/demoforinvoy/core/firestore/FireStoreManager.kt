package com.dinesh.demoforinvoy.core.firestore

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.core.accountmanager.AccountManager
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.datamodels.user.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject

class FireStoreManager @Inject constructor(private val accountManager: AccountManager) {

    private val instance: FirebaseFirestore by lazy { Firebase.firestore }

    private lateinit var userDocs: DocumentReference

    private var userData: LiveData<User>? = null
    private var userDataObserver: Observer<User>? = null

    init {
        userData = accountManager.observeUserData()
        userDataObserver = Observer<User> {
            if (it != User.UNKNOWN_USER) initializeForUser(it)
        }.also { userData?.observeForever(it) }
    }

    private fun initializeForUser(user: User) {
        userDocs = instance.document("user/${user.userId}")
        userDocs.set(user, SetOptions.merge())
    }

    fun initializeChat(
        toUser: User = User(name = "Coach", userId = "leault3QkURTlm0HTE7VBu257uA2"),
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val currentUser = accountManager.getCurrentUser().guardAgainstNull { return onFailure() }

        instance
            .document("messages/${currentUser.userId}/${toUser.userId}")
            .get(Source.SERVER)
            .addOnCompleteListener {
                when {
                    it.exception != null -> {
                        Timber.d("Dinesh - Failed: ${it.exception}")
                        onFailure()
                    }
                    else -> {
                        Timber.d("Dinesh - ${it.result.exists()}")
                        onSuccess()
                    }
                }
            }
    }

    fun clearReferences() {
        userDataObserver?.let { userData?.removeObserver(it) }
        userDataObserver = null
        userData = null
    }
}