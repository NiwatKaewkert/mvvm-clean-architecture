package me.niwat.mvvm.di

import me.niwat.mvvm.presenter.camera.CameraFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {  }

val useCaseModule = module {  }

val viewModelModule = module {
    viewModel {
        CameraFragmentViewModel()
    }
}