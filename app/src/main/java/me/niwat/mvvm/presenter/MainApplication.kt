package me.niwat.mvvm.presenter

import android.app.Application
import me.niwat.mvvm.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(
                viewModelModule,
                useCaseModule,
                apiModule,
                repositoryModule,
                retrofitModule
            )
        }
    }
}