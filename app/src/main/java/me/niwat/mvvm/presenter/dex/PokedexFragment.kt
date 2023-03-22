package me.niwat.mvvm.presenter.dex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import me.niwat.mvvm.base.BaseFragment
import me.niwat.mvvm.domain.models.Card
import me.niwat.mvvm.databinding.FragmentPokedexBinding
import me.niwat.mvvm.presenter.dex.adapter.PokedexAdapter
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PokedexFragment :
    BaseFragment<FragmentPokedexBinding, PokedexFragmentViewModel>(FragmentPokedexBinding::inflate) {
    override val viewModel: PokedexFragmentViewModel by activityViewModel()
    private val pokedexList: MutableList<Card> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun init() {
        viewModel.getPokemonList()
    }

    override fun updateUI(view: View, savedInstanceState: Bundle?) {
        binding.recyclePokedex.adapter = PokedexAdapter(pokedexList)
    }

    override fun observer() {
        viewModel.pokemonListLiveData.observe(this) { result ->
            binding.recyclePokedex.adapter?.notifyDataSetChanged()
            result?.let {
                pokedexList.addAll(it)
                binding.recyclePokedex.adapter?.notifyItemRangeChanged(0, pokedexList.size)
            }
        }
    }

    companion object {
        private var TAG = PokedexFragment::class.java.simpleName
        fun newInstance(): Fragment = PokedexFragment()
    }
}