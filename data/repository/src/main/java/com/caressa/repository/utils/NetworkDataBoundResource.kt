package com.caressa.repository.utils

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import timber.log.Timber
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext

abstract class NetworkDataBoundResource<ResultType, RequestType> {
    private val result = MutableLiveData<Resource<ResultType>>()
    private val supervisorJob = SupervisorJob()

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    suspend fun build(): NetworkDataBoundResource<ResultType, RequestType> {
        withContext(Dispatchers.Main) {
            result.value = Resource.loading(null)
        }
        CoroutineScope(coroutineContext).launch(supervisorJob) {

            try {
                fetchFromNetwork()
            } catch (e: UnknownHostException) {
                Timber.e("An error happened: $e")
                setValue(
                    Resource.error(
                        e,
                        null,
                        errorMessage = "Seems like you are offline. Please check your internet connection and try again."
                    )
                )
            } catch (e: Exception) {
                Timber.e("An error happened: $e")
                setValue(Resource.error(e, null, errorMessage = "Something went wrong."))
            }

        }
        return this
    }

    private suspend fun fetchFromNetwork() {

        val apiResponse = createCallAsync().await()
        Timber.e("Data fetched from network")
        setValue(Resource.success(processResponse(apiResponse)))
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) result.postValue(newValue)
    }

    @WorkerThread
    protected abstract fun processResponse(response: RequestType): ResultType

    @MainThread
    protected abstract fun createCallAsync(): Deferred<RequestType>
}