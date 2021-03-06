package com.dinesh.demoforinvoy.viewmodel.chat

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.core.livedata.LiveDataResponse
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.core.scheduler.SchedulerProvider
import com.dinesh.demoforinvoy.datamodels.message.Message
import com.dinesh.demoforinvoy.repositories.ChatRepository
import com.dinesh.demoforinvoy.ui.chat.ChatMessagePresentationModel
import com.dinesh.demoforinvoy.viewmodel.BaseViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val schedulerProvider: SchedulerProvider
): BaseViewModel() {

    private val messageSentData = MutableLiveData<LiveDataResponse<Pair<String, ChatMessagePresentationModel>>>()

    private var currentPage = 1

    private var messagesObserver: Observer<LiveDataResponse<List<Message>>>? = null
    private lateinit var messagesObservable: LiveData<LiveDataResponse<List<Message>>>

    private val messagesData = MutableLiveData<LiveDataResponse<Pair<Int, List<ChatMessagePresentationModel>>>>()

    private var chatWithUserId: String? = null
    private var chatWithUserToken: String? = null

    init {
        messagesObserver?.let { messagesObservable.removeObserver(it) }
        messagesObservable = chatRepository.getMessagesData()
        messagesObservable.observeForever(getMessagesObserver())
    }

    override fun refreshData() {
        super.refreshData()
        triggerFetchMessages(currentPage)
    }

    fun initializeForUser(userId: String, token: String) {
        chatWithUserId = userId
        chatWithUserToken = token
    }

    private fun triggerFetchMessages(forPage: Int) {
        chatRepository.fetchMessages(forPage, chatWithUserId)
    }

    private fun getMessagesObserver(): Observer<LiveDataResponse<List<Message>>> {
        return messagesObserver ?: Observer<LiveDataResponse<List<Message>>> { response ->
            when {
                response.isLoading -> messagesData.postValue(LiveDataResponse(data = Pair(currentPage, listOf()), isLoading = true))
                response.errorMessage != null -> messagesData.postValue(
                    LiveDataResponse(errorMessage = response.errorMessage, isLoading = false)
                )
                response.data != null -> LiveDataResponse(
                    data = Pair(
                        currentPage,
                        response.data.map { ChatMessagePresentationModel.fromMessage(it) }
                    ),
                    isLoading = false
                ).also {
                    messagesData.postValue(it)
                }
            }
        }.also {
            messagesObserver = it
        }
    }

    fun getNextPage() {
        triggerFetchMessages(++currentPage)
    }

    private fun clearExistingMessagesObserver() {
        if (::messagesObservable.isInitialized) {
            messagesObserver?.let { messagesObservable.removeObserver(it) }
            messagesObserver = null
        }
    }

    fun sendMessage(message: String) {
        val tempId = UUID.randomUUID().toString()
        val tempMessage = ChatMessagePresentationModel(
            id = tempId,
            message = message,
            sentOn = SynchronizedTimeUtils.getFormattedTimeWithDateNoYearNoSec(Date(), TimeZone.getDefault()),
            isSentMessage = true
        )
        val observable = chatRepository.sendMessage(message, chatWithUserId, chatWithUserToken)
        var observer: Observer<LiveDataResponse<Message>>? = null
        observer = Observer<LiveDataResponse<Message>> { response ->

            when {
                response.isLoading -> messageSentData.postValue(
                    LiveDataResponse(data = Pair(tempId, tempMessage.copy(isLoading = true)), isLoading = true)
                )
                response.errorMessage != null -> {
                    schedulerProvider.computation().scheduleDirect({
                        messageSentData.postValue(
                            LiveDataResponse(
                                data = Pair(tempId, tempMessage.copy(sendFailed = true)),
                                errorMessage = response.errorMessage,
                                isLoading = false
                            )
                        )
                    }, 1000, TimeUnit.MILLISECONDS)
                    observer?.let { it -> observable.removeObserver(it) }
                }
                response.data != null -> {
                    schedulerProvider.computation().scheduleDirect({
                        ChatMessagePresentationModel.fromMessage(response.data).also {
                            messageSentData.postValue(LiveDataResponse(Pair(tempId, it), isLoading = false))
                        }
                    }, 1000, TimeUnit.MILLISECONDS)
                    observer?.let { it -> observable.removeObserver(it) }
                }
            }
        }
        observable.observeForever(observer)
    }

    fun getMessageSentData(): LiveData<LiveDataResponse<Pair<String, ChatMessagePresentationModel>>> = messageSentData

    fun getConversationData(): LiveData<LiveDataResponse<Pair<Int, List<ChatMessagePresentationModel>>>> = messagesData

    fun receivedNewMessage(it: Bundle) {
        val message = it.getString("message").guardAgainstNull { return }
        val messageId = it.getString("messageId").guardAgainstNull { return }
        val sentOn = it.getLong("sentOn").guardAgainstNull { return }
        ChatMessagePresentationModel(
            id = messageId,
            message = message,
            isSentMessage = false,
            sentOn = SynchronizedTimeUtils.getFormattedTimeWithDateNoYearNoSec(
                Date(sentOn),
                TimeZone.getDefault()
            )
        ).also {
            messageSentData.postValue(LiveDataResponse(Pair(messageId, it), isLoading = false))
        }
    }

    override fun clearReferences() {
        clearExistingMessagesObserver()
    }

}