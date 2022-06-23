package com.dinesh.demoforinvoy.ui.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.SynchronizedTimeUtils
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.core.misc.isLastItemShowing
import com.dinesh.demoforinvoy.core.misc.isValidToCheckForLastItem
import com.dinesh.demoforinvoy.databinding.FragmentChatBinding
import com.dinesh.demoforinvoy.databinding.FullscreenBlockingLoadingBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.chat.ChatViewModel
import timber.log.Timber
import java.util.*

class ChatFragment: BaseDaggerFragment<ChatViewModel>(), MenuProvider {

    private var binding: FragmentChatBinding? = null

    private var textChangedListener: TextWatcher? = null

    private var adapter: ChatAdapter? = null

    private val args by navArgs<ChatFragmentArgs>()

    override val fragmentLayoutId: Int = R.layout.fragment_chat

    override fun viewModel(): ChatViewModel {
        return ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            requireActivity().addMenuProvider(this@ChatFragment, viewLifecycleOwner)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setHomeButtonEnabled(false)
            setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> false
        }
    }

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
        binding = FragmentChatBinding.bind(view)
        bufferingBinding = FullscreenBlockingLoadingBinding.bind(view)
    }

    override fun initListeners() {
        super.initListeners()
        textChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                binding?.chatSend?.isEnabled = !s.isNullOrBlank()
            }
        }
    }

    override fun attachListeners() {
        super.attachListeners()
        val binding = binding.guardAgainstNull { return }

        binding.chatSend.setOnClickListener {
            viewModel.sendMessage(binding.chatInput.text.toString())
            binding.chatInput.text.clear()
        }

        textChangedListener?.let { binding.chatInput.addTextChangedListener(it) }

        attachMessagesListScrollListener(binding)
    }

    private fun attachMessagesListScrollListener(binding: FragmentChatBinding) {
        binding.listChat.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (recyclerView.isValidToCheckForLastItem(newState) &&
                        recyclerView.isLastItemShowing() &&
                        adapter?.isWaitingForMoreResults() != true
                    ) { handleLoadMoreMessages() }
                }
            }
        )
    }

    private fun handleLoadMoreMessages() {
        adapter?.loadingMore()
        viewModel.getNextPage()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        args.userId?.let { userId -> args.userToken?.let { userToken -> viewModel.initializeForUser(userId, userToken) } }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setup() {
        super.setup()
        setUpList()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.extras?.let {
                        val message = it.getString("message").guardAgainstNull { return }
                        val messageId = it.getString("messageId").guardAgainstNull { return }
                        val sentOn = it.getLong("sentOn").guardAgainstNull { return }
                        adapter?.updateMessage(
                            Pair(
                                messageId,
                                ChatMessagePresentationModel(
                                    id = messageId,
                                    message = message,
                                    isSentMessage = false,
                                    sentOn = SynchronizedTimeUtils.getFormattedTimeWithDateNoYearNoSec(
                                        Date(sentOn),
                                        TimeZone.getDefault()
                                    )
                                )
                            )
                        )
                        binding?.listChat?.scrollToPosition(0)
                        Timber.d("Dinesh: Received message - $it")
                    }
                }
            },
            IntentFilter("New Message")
        )
    }

    private fun setUpList() {
        adapter = ChatAdapter()
        binding?.listChat?.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
            it.itemAnimator = null
        }

        binding?.chatSend?.isEnabled = false
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.getMessageSentData().observe(viewLifecycleOwner) {
            when (it.data) {
                null -> Unit
                else -> {
                    adapter?.updateMessage(it.data)
                    binding?.listChat?.scrollToPosition(0)
                }
            }
        }

        viewModel.getConversationData().observe(viewLifecycleOwner) {
            when {
                it.isLoading -> if ((adapter?.itemCount ?: 0) > 0) adapter?.loadingMore() else startLoading()
                it.errorMessage != null -> {
                    stopLoading()
                    if ((adapter?.itemCount ?: 0) == 0) showErrorScreen()
                    adapter?.notifyEndOfResults()
                }
                it.data != null -> {
                    stopLoading()
                    adapter?.updateData(it.data.second, it.data.first)
                    if (it.data.second.size < 20) adapter?.notifyEndOfResults()
                }
            }
        }
    }

    private fun showErrorScreen() {

    }

    override fun clearListeners() {
        super.clearListeners()
        textChangedListener?.let { binding?.chatInput?.removeTextChangedListener(it) }
    }

    override fun clearViewBindings() {
        super.clearViewBindings()
        binding = null
    }

    override fun clearReferences() {
        super.clearReferences()
        textChangedListener = null
        adapter = null
    }

}