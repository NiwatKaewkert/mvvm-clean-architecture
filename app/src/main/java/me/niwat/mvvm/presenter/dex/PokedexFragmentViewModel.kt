package me.niwat.mvvm.presenter.dex

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.niwat.mvvm.base.BaseViewModel
import me.niwat.mvvm.data.models.Card
import me.niwat.mvvm.data.network.ServiceResult
import me.niwat.mvvm.domain.usecase.GetPokemonListUseCase

class PokedexFragmentViewModel(private val getPokemonListUseCase: GetPokemonListUseCase) :
    BaseViewModel() {
    val pokemonListLiveData: MutableLiveData<MutableList<Card>> =
        MutableLiveData()

    fun getPokemonList() {
        viewModelScope.launch {
            loadingLiveData.postValue(true)
            val result = withContext(Dispatchers.IO) {
                getPokemonListUseCase.execute(Unit)
            }

            when (result) {
                is ServiceResult.Success -> {
                    loadingLiveData.postValue(false)
                    pokemonListLiveData.postValue(result.data.cards)
                }
                is ServiceResult.Error -> {
                    loadingLiveData.postValue(false)
                    errorMessageLiveData.postValue(result.exception)
                }
            }
        }
    }
}