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
    private lateinit var conversationWithCoachDocs: DocumentReference

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

    private fun getUserChatDocs(currentUser: User, chatWithUserId: String?): DocumentReference {
        return instance.document("messages/${chatWithUserId ?: currentUser.userId}")
    }

    fun sendMessage(
        message: String,
        chatWithUserId: String?,
        onSuccess: (Message) -> Unit,
        onFailure: () -> Unit
    ) {

        val currentUser = accountManager.getCurrentUser().guardAgainstNull { return onFailure() }
        val sentOn = Date().time
        getUserChatDocs(currentUser, chatWithUserId).collection(USER_COACH.userId).add(
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
                    addCoachConversationToDb(chatWithUserId)
                }
                it.exception != null -> onFailure()
            }
        }
    }

    private fun addCoachConversationToDb(chatWithUserId: String?) {
        val currentUser = accountManager.getCurrentUser().guardAgainstNull { return }

        if (!::conversationWithCoachDocs.isInitialized) {
            conversationWithCoachDocs = instance.document("coach/${USER_COACH.userId}/conversations/${chatWithUserId ?: currentUser.userId}")
        }

        conversationWithCoachDocs.set(mapOf("temp" to true), SetOptions.merge())
    }

    fun getMessages(forPage: Int, chatWithUserId: String?, onSuccess: (List<Message>) -> Unit, onFailure: () -> Unit) {

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
            getNextPageOfMessages(currentUser, chatWithUserId, onSuccess, onFailure, docRef)
        }
    }

    private fun getNextPageOfMessages(
        currentUser: User,
        chatWithUserId: String?,
        onSuccess: (List<Message>) -> Unit,
        onFailure: () -> Unit,
        startAfter: DocumentSnapshot? = null
    ) {

        val query = getUserChatDocs(currentUser, chatWithUserId)
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

    fun getCoachConversations(onSuccess: (List<User>) -> Unit, onFailure: () -> Unit) {
        instance
            .collection("coach/${USER_COACH.userId}/conversations/")
            .get(Source.SERVER)
            .addOnCompleteListener { taskConversations ->
                when {
                    taskConversations.isSuccessful -> {
                        instance.collection("user")
                            .whereIn(
                                FieldPath.documentId(),
                                taskConversations.result.documents.map { documentSnapshot -> documentSnapshot.id }
                            )
                            .get()
                            .addOnCompleteListener { usersTask ->
                                when {
                                    usersTask.isSuccessful -> {
                                        usersTask.result.documents.map {
                                            User(
                                                name = it["name"]?.toString() ?: "",
                                                userId = it["userId"]?.toString() ?: ""
                                            )
                                        }.also {
                                            onSuccess(it)
                                        }
                                    }
                                    else -> onFailure()
                                }
                            }
                    }
                    else -> onFailure()
                }
            }
    }

    fun clearReferences() {
        userDataObserver?.let { userData?.removeObserver(it) }
        userDataObserver = null
        userData = null
    }
}