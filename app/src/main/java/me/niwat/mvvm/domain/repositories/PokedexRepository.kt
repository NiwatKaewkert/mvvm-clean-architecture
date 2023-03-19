package me.niwat.mvvm.domain.repositories

import me.niwat.mvvm.data.models.PokemonListResponseModel
import retrofit2.Response

interface PokedexRepository {
    suspend fun getPokemonList(): Response<PokemonListResponseModel>
}