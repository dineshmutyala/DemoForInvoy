package com.dinesh.demoforinvoy.core.firebase

import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(private val accountManager: AccountManager) {

    private val instance: FirebaseStorage by lazy { Firebase.storage }

    private val storageReference: StorageReference by lazy {
        val currentUser = accountManager.getCurrentUser().guardAgainstNull { throw NullPointerException("User not logged in. You shouldn't be here!!") }
        instance.reference.child("images/${currentUser.userId}/")
    }

    fun uploadGraphImage(bytes: ByteArray, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        storageReference
            .child("${UUID.randomUUID()}.jpg")
            .putBytes(bytes)
            .addOnCompleteListener { uploadTask ->
                when {
                    uploadTask.isSuccessful -> {
                        uploadTask.result.storage.downloadUrl.addOnCompleteListener { uriTask ->
                            when {
                                uriTask.isSuccessful -> onSuccess(uriTask.result.toString())
                                uriTask.exception != null -> onFailure(uriTask.exception.toString())
                            }
                        }
                    }
                    uploadTask.exception != null -> onFailure(uploadTask.exception.toString())
                }
            }
    }
}