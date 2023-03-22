package me.niwat.mvvm.data.network

import me.niwat.mvvm.domain.models.PokemonListResponseModel
import retrofit2.Response
import retrofit2.http.POST

interface ClientServices {
    @POST("v3/f9916417-f92e-478e-bfbc-c39e43f7c75b")
    suspend fun getPokemonList(): Response<PokemonListResponseModel>
}