package com.dinesh.demoforinvoy.core.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.core.firebase.AccountManager.Companion.USER_COACH
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.datamodels.message.Message
import com.dinesh.demoforinvoy.datamodels.user.User
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreManager @Inject constructor(private val accountManager: AccountManager) {

    private val instance: FirebaseFirestore by lazy { Firebase.firestore }

    private lateinit var userDocs: DocumentReference
    private lateinit var coachChatDocs: DocumentReference
    private lateinit var coachConversationDocs: DocumentReference

    private var userData: LiveData<User>? = null
    private var userDataObserver: Observer<User>? = null

    private var docRefForLastItemInMostRecentPage: DocumentSnapshot? = null

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

    fun initializeChat(onSuccess: () -> Unit, onFailure: () -> Unit) {
        val currentUser = accountManager.getCurrentUser().guardAgainstNull { return onFailure() }

        if (!::coachChatDocs.isInitialized) {
            coachChatDocs = instance.document("messages/${currentUser.userId}")
        }
        coachChatDocs
            .get(Source.SERVER)
            .addOnCompleteListener {
                when {
                    it.exception != null -> onFailure()
                    else -> onSuccess()
                }
            }
    }

    fun sendMessageToCoach(
        message: String,
        onSuccess: (Message) -> Unit,
        onFailure: () -> Unit
    ) {

        val currentUser = accountManager.getCurrentUser().guardAgainstNull { return onFailure() }
        val sentOn = Date().time
        coachChatDocs.collection(USER_COACH.userId).add(
            mapOf(
                "message" to message,
                "sentOn" to sentOn,
                "sentBy" to currentUser.userId
            )
        ).addOnCompleteListener {
            when {
                it.isSuccessful -> {
                    onSuccess(
                        Message(
                            id = it.result.id,
                            message = message,
                            sentOn = sentOn,
                            isSentMessage = true
                        )
                    )
                    addCoachConversationToDb()
                }
                it.exception != null -> onFailure()
            }
        }
    }

    private fun addCoachConversationToDb() {
        val currentUser = accountManager.getCurrentUser().guardAgainstNull { return }

        if (!::coachConversationDocs.isInitialized) {
            coachConversationDocs = instance.document("coach/${USER_COACH.userId}/conversations/${currentUser.userId}")
        }

        coachConversationDocs.set(mapOf("temp" to true), SetOptions.merge())
    }

    fun getMessages(forPage: Int, onSuccess: (List<Message>) -> Unit, onFailure: () -> Unit) {

        val currentUser = accountManager.getCurrentUser().guardAgainstNull { return }

//        CoroutineScope(Dispatchers.IO).launch {
//            for(i in 1..100) {
//                delay(1000)
//                val sentOn = Date().time
//                coachChatDocs.collection(USER_COACH.userId).add(
//                    mapOf(
//                        "message" to "Message #$i",
//                        "sentOn" to sentOn,
//                        "sentBy" to if (i % 2 == 0) currentUser.userId else USER_COACH.userId
//                    )
//                )
//            }
//        }

        (if (forPage == 1) null else docRefForLastItemInMostRecentPage).also { docRef ->
            getNextPageOfMessages(currentUser, onSuccess, onFailure, docRef)
        }
    }

    private fun getNextPageOfMessages(
        currentUser: User,
        onSuccess: (List<Message>) -> Unit,
        onFailure: () -> Unit,
        startAfter: DocumentSnapshot? = null
    ) {

        val query = coachChatDocs
            .collection(USER_COACH.userId)
            .orderBy("sentOn", Query.Direction.DESCENDING)
            .limit(20)
            .let { query-> startAfter?.let { query.startAfter(it) } ?: query }

        query.get(Source.SERVER).addOnSuccessListener { snapshot ->
            snapshot.documents.mapNotNull {
                it.data?.let { data ->
                    Message(
                        id = it.id,
                        message = data.getValue("message").toString(),
                        sentOn = data.getValue("sentOn").toString().toLong(),
                        isSentMessage = data.getValue("sentBy").toString() == currentUser.userId
                    )
                }
            }.also { onSuccess(it) }

            docRefForLastItemInMostRecentPage = snapshot.documents.lastOrNull()

        }.addOnFailureListener {
            onFailure()
        }
    }

    fun clearReferences() {
        userDataObserver?.let { userData?.removeObserver(it) }
        userDataObserver = null
        userData = null
    }
}