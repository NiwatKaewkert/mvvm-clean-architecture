package me.niwat.mvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding, VM : BaseViewModel>(private val bindingInflater: (layoutInflater: LayoutInflater) -> T) :
    Fragment() {

    protected abstract val viewModel: VM
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflater.invoke(inflater)
        observer()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(view, savedInstanceState)
    }

    abstract fun init()

    abstract fun updateUI(view: View, savedInstanceState: Bundle?)

    abstract fun observer()
}
