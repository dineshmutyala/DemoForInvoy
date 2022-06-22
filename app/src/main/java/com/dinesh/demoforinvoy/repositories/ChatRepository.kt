package com.dinesh.demoforinvoy.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dinesh.demoforinvoy.core.firebase.FireStoreManager
import com.dinesh.demoforinvoy.core.firebase.StorageManager
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.datamodels.message.Message
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val fireStore: FireStoreManager,
    private val storageManager: StorageManager
) {

    private val messagesData = MutableLiveData<LiveDataResponse<List<Message>>>()

    init {
        fireStore.initializeChat(
            onSuccess = {},
            onFailure = {}
        )
    }

    fun sendMessageToCoach(message: String): LiveData<LiveDataResponse<Message>> {
        return MutableLiveData<LiveDataResponse<Message>>(LiveDataResponse(isLoading = true)).also { observable ->
            fireStore.sendMessageToCoach(
                message,
                { observable.postValue(LiveDataResponse(data = it, isLoading = false)) },
                { observable.postValue(LiveDataResponse(errorMessage = "Could not send message", isLoading = false)) }
            )
        }
    }

    fun fetchMessages(forPage: Int) {
        messagesData.postValue(LiveDataResponse(isLoading = true))
        fireStore.getMessages(
            forPage,
            { messagesData.postValue(LiveDataResponse(data = it, isLoading = false)) },
            { messagesData.postValue(LiveDataResponse(errorMessage = "failed to fetch Messages", isLoading = false)) }
        )
    }

    fun getMessagesData(): LiveData<LiveDataResponse<List<Message>>> = messagesData

    fun sendImageToCoach(byteArray: ByteArray): LiveData<LiveDataResponse<Message>> {

        return MutableLiveData<LiveDataResponse<Message>>(LiveDataResponse(isLoading = true)).also { observable ->
            storageManager.uploadGraphImage(
                byteArray,
                { downloadPath ->
                    fireStore.sendMessageToCoach(
                        downloadPath,
                        { observable.postValue(LiveDataResponse(data = it, isLoading = false)) },
                        { observable.postValue(LiveDataResponse(errorMessage = "Could not send message", isLoading = false)) }
                    )
                },
                { observable.postValue(LiveDataResponse(errorMessage = it, isLoading = false)) }
            )
        }
    }

}