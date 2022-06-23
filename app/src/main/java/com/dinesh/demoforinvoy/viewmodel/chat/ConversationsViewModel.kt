package com.dinesh.demoforinvoy.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.datamodels.user.User
import com.dinesh.demoforinvoy.repositories.ChatRepository
import com.dinesh.demoforinvoy.ui.chat.ChatMessagePresentationModel
import com.dinesh.demoforinvoy.ui.chat.ConversationPresentationModel
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import javax.inject.Inject

class ConversationsViewModel @Inject constructor(
    private val chatRepository: ChatRepository
): BaseViewModel() {

    private var conversationsObserver: Observer<LiveDataResponse<List<User>>>? = null
    private lateinit var conversationsObservable: LiveData<LiveDataResponse<List<User>>>

    private val conversationsData = MutableLiveData<LiveDataResponse<List<ConversationPresentationModel>>>()

    init {
        conversationsObserver?.let { conversationsObservable.removeObserver(it) }
        conversationsObservable = chatRepository.getConversationsData()
        conversationsObservable.observeForever(getConversationsObserver())
    }

    override fun refreshData() {
        super.refreshData()
        chatRepository.getConversations()
    }

    private fun getConversationsObserver(): Observer<LiveDataResponse<List<User>>> {
        return conversationsObserver ?: Observer<LiveDataResponse<List<User>>> { response ->
            when {
                response.isLoading -> conversationsData.postValue(LiveDataResponse(data = listOf(), isLoading = true))
                response.errorMessage != null -> conversationsData.postValue(
                    LiveDataResponse(errorMessage = response.errorMessage, isLoading = false)
                )
                response.data != null -> LiveDataResponse(
                    data = response.data.map { ConversationPresentationModel.fromUser(it) },
                    isLoading = false
                ).also {
                    conversationsData.postValue(it)
                }
            }
        }.also {
            conversationsObserver = it
        }
    }

    fun getConversationData(): LiveData<LiveDataResponse<List<ConversationPresentationModel>>> = conversationsData

    override fun clearReferences() {}
}