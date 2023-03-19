package me.niwat.mvvm.domain.usecase

import me.niwat.mvvm.base.BaseUseCase
import me.niwat.mvvm.data.models.PokemonListResponseModel
import me.niwat.mvvm.data.network.ServiceResult
import me.niwat.mvvm.data.repositories.PokedexRepositoryImpl

class GetPokemonListUseCase(private val pokedexRepository: PokedexRepositoryImpl) :
    BaseUseCase<Unit, PokemonListResponseModel>() {
    override suspend fun execute(parameter: Unit): ServiceResult<PokemonListResponseModel> {
        return try {
            val response = pokedexRepository.getPokemonList()
            if (isResponseSuccess(response)) {
                ServiceResult.Success(response.body() ?: PokemonListResponseModel())
            } else {
                ServiceResult.Error(response.message() ?: "")
            }
        } catch (e: Exception) {
            ServiceResult.Error(e.message.toString())
        }
    }
}