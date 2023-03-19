package me.niwat.mvvm.di

import me.niwat.mvvm.data.network.ClientServices
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single(createdAtStart = false) { get<Retrofit>().create(ClientServices::class.java) }
}