package me.niwat.mvvm.di

import me.niwat.mvvm.data.repositories.PokedexRepositoryImpl
import me.niwat.mvvm.domain.usecase.GetPokemonListUseCase
import me.niwat.mvvm.presenter.dex.PokedexFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module { single { PokedexRepositoryImpl(get()) } }

val useCaseModule = module { single { GetPokemonListUseCase(get()) } }

val viewModelModule = module { viewModel { PokedexFragmentViewModel(get()) } }