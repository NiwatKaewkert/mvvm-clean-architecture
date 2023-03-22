package me.niwat.mvvm.data.repositories

import me.niwat.mvvm.domain.models.PokemonListResponseModel
import me.niwat.mvvm.data.network.ClientServices
import me.niwat.mvvm.domain.repositories.PokedexRepository
import retrofit2.Response

class PokedexRepositoryImpl(private val apiService: ClientServices) : PokedexRepository {
    override suspend fun getPokemonList(): Response<PokemonListResponseModel> {
        return apiService.getPokemonList()
    }
}