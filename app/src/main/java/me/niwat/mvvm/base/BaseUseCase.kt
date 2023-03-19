package me.niwat.mvvm.base

import me.niwat.mvvm.data.network.ServiceResult
import retrofit2.Response

abstract class BaseUseCase<in P, R> {
    abstract suspend fun execute(parameter: P): ServiceResult<R>
    fun <T> isResponseSuccess(response: Response<T>): Boolean {
        return (response.code() == 200 && response.body() != null)
    }
}