package com.dinesh.demoforinvoy.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dinesh.demoforinvoy.core.firebase.FireStoreManager
import com.dinesh.demoforinvoy.core.firebase.StorageManager
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.datamodels.message.Message
import com.dinesh.demoforinvoy.datamodels.user.User
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val fireStore: FireStoreManager,
    private val storageManager: StorageManager
) {

    private val messagesData = MutableLiveData<LiveDataResponse<List<Message>>>()
    private val conversationsData = MutableLiveData<LiveDataResponse<List<User>>>()

    fun sendMessage(message: String, chatWithUserId: String?): LiveData<LiveDataResponse<Message>> {
        return MutableLiveData<LiveDataResponse<Message>>(LiveDataResponse(isLoading = true)).also { observable ->
            fireStore.sendMessage(
                message,
                chatWithUserId,
                { observable.postValue(LiveDataResponse(data = it, isLoading = false)) },
                { observable.postValue(LiveDataResponse(errorMessage = "Could not send message", isLoading = false)) }
            )
        }
    }

    fun fetchMessages(forPage: Int, chatWithUserId: String?) {
        messagesData.postValue(LiveDataResponse(isLoading = true))
        fireStore.getMessages(
            forPage,
            chatWithUserId,
            { messagesData.postValue(LiveDataResponse(data = it, isLoading = false)) },
            { messagesData.postValue(LiveDataResponse(errorMessage = "failed to fetch Messages", isLoading = false)) }
        )
    }

    fun getConversations() {
        conversationsData.postValue(LiveDataResponse(isLoading = true))
        fireStore.getCoachConversations(
            { conversationsData.postValue(LiveDataResponse(data = it, isLoading = false)) },
            { conversationsData.postValue(LiveDataResponse(errorMessage = "failed to fetch Messages", isLoading = false)) }
        )
    }

    fun getMessagesData(): LiveData<LiveDataResponse<List<Message>>> = messagesData
    fun getConversationsData(): LiveData<LiveDataResponse<List<User>>> = conversationsData

    fun sendImageToCoach(byteArray: ByteArray): LiveData<LiveDataResponse<Message>> {

        return MutableLiveData<LiveDataResponse<Message>>(LiveDataResponse(isLoading = true)).also { observable ->
            storageManager.uploadGraphImage(
                byteArray,
                { downloadPath ->
                    fireStore.sendMessage(
                        downloadPath,
                        null,
                        { observable.postValue(LiveDataResponse(data = it, isLoading = false)) },
                        { observable.postValue(LiveDataResponse(errorMessage = "Could not send message", isLoading = false)) }
                    )
                },
                { observable.postValue(LiveDataResponse(errorMessage = it, isLoading = false)) }
            )
        }
    }

}