package me.niwat.mvvm.presenter.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.niwat.mvvm.databinding.FragmentLandingBinding
import me.niwat.mvvm.presenter.MainActivity

class LandingFragment : Fragment() {
    private lateinit var binding: FragmentLandingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLandingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonTakePhoto.setOnClickListener {
                (activity as MainActivity).requestPermission()
            }
        }
    }

    companion object {
        private var TAG = LandingFragment::class.java.simpleName
        fun newInstance(): Fragment = LandingFragment()
    }
}