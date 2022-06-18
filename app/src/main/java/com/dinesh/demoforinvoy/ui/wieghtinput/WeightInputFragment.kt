package com.dinesh.demoforinvoy.ui.wieghtinput

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.dinesh.demoforinvoy.R
import com.dinesh.demoforinvoy.core.misc.guardAgainstNull
import com.dinesh.demoforinvoy.databinding.FragmentWeightInputBinding
import com.dinesh.demoforinvoy.ui.BaseDaggerFragment
import com.dinesh.demoforinvoy.viewmodel.wieghtinput.WeightInputViewModel

class WeightInputFragment: BaseDaggerFragment<WeightInputViewModel>() {

    private var binding: FragmentWeightInputBinding? = null

    private var textChangedListener: TextWatcher? = null

    private val navArgs: WeightInputFragmentArgs by navArgs()

    override val fragmentLayoutId: Int = R.layout.fragment_weight_input

    override fun viewModel(): WeightInputViewModel {
        return ViewModelProvider(this, viewModelFactory)[WeightInputViewModel::class.java]
    }

    override fun initViewBindings(view: View) {
        super.initViewBindings(view)
        binding = FragmentWeightInputBinding.bind(view)
    }

    override fun initListeners() {
        super.initListeners()
        textChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                binding?.updateCTA?.isEnabled = !s.isNullOrBlank()
            }
        }
    }

    override fun attachListeners() {
        super.attachListeners()
        val binding = binding.guardAgainstNull { return }
        textChangedListener?.let { binding.weightInput.addTextChangedListener(it) }
        binding.updateCTA.setOnClickListener {  }
    }

    override fun setup() {
        super.setup()
        binding?.enterWeightTitle?.text = getString(R.string.enter_your_weight_on, navArgs.forDate)
    }

    override fun setupObservers() {
        super.setupObservers()
    }

    override fun clearListeners() {
        super.clearListeners()
        val binding = binding.guardAgainstNull { return }
        textChangedListener?.let { binding.weightInput.removeTextChangedListener(it) }
        binding.updateCTA.setOnClickListener(null)
    }

    override fun clearViewBindings() {
        super.clearViewBindings()
        binding = null
    }

    override fun clearReferences() {
        super.clearReferences()
        textChangedListener = null
    }
}