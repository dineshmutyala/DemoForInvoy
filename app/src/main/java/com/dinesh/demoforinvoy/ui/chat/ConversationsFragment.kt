package com.dinesh.demoforinvoy.ui.chat

import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.events.Event
import com.dinesh.demoforinvoy.core.events.EventListener
import com.dinesh.demoforinvoy.databinding.FragmentConversationsBinding
import com.dinesh.demoforinvoy.databinding.FullscreenBlockingLoadingBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.chat.ConversationsViewModel
import timber.log.Timber

class ConversationsFragment: BaseDaggerFragment<ConversationsViewModel>() {

    private var binding: FragmentConversationsBinding? = null

    private var adapter: ConversationAdapter? = null

    private var eventListener: EventListener? = null

    override val fragmentLayoutId: Int = R.layout.fragment_conversations

    override fun viewModel(): ConversationsViewModel {
        return ViewModelProvider(this, viewModelFactory)[ConversationsViewModel::class.java]
    }

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
        binding = FragmentConversationsBinding.bind(view)
        bufferingBinding = FullscreenBlockingLoadingBinding.bind(view)
    }

    override fun initListeners() {
        super.initListeners()
        eventListener = object : EventListener {
            override fun onEvent(event: Event) {
                when(event) {
                    is ViewConversationEvent -> {
                        findNavController().navigate(
                            ConversationsFragmentDirections.actionConversationsFragmentToChatFragment(
                                event.forUserWithId
                            )
                        )
                    }
                }
            }
        }
    }

    override fun attachListeners() {
        super.attachListeners()
    }

    override fun setup() {
        super.setup()
        binding?.listConversations?.also {
            it.adapter = ConversationAdapter(eventListener).also { adapter -> this.adapter = adapter }
            it.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.getConversationData().observe(viewLifecycleOwner) {
            when {
                it.isLoading -> startLoading()
                it.errorMessage != null -> {
                    stopLoading()
                    Timber.d(it.errorMessage)
                }
                it.data != null -> {
                    stopLoading()
                    adapter?.updateData(it.data)
                }
            }
        }
    }

    override fun clearListeners() {
        super.clearListeners()
        eventListener = null
    }

    override fun clearViewBindings() {
        super.clearViewBindings()
        binding = null
    }

    override fun clearReferences() {
        super.clearReferences()
        adapter?.clearReferences()
        adapter = null
    }
}