package me.niwat.mvvm.di

import com.google.gson.GsonBuilder
import me.niwat.mvvm.BuildConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECTION_TIME_OUT = 15L
private const val READ_CONNECTION_TIME_OUT = 15L
private const val WRITE_CONNECTION_TIME_OUT = 15L

val retrofitModule = module {
    single { Cache(androidApplication().cacheDir, 10L * 1024 * 1024) }
    single { GsonBuilder().setPrettyPrinting().create() }
    single { retrofitHttpClient() }
    single { retrofitBuilder() }
}

private fun Scope.retrofitBuilder(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(get()))
        .client(get())
        .build()
}

private fun Scope.retrofitHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().apply {
        cache(get())
        connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
        readTimeout(READ_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
        addInterceptor(Interceptor { chain ->
            chain.proceed(chain.request().newBuilder().apply {
                header("Accept", "application/json")
            }.build())
        })
        addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
    }.build()
}
