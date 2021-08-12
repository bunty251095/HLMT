package com.caressa.repository.utils

import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.caressa.repository.R
import kotlinx.coroutines.*
import timber.log.Timber
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext

abstract class NetworkDataBoundResource<ResultType, RequestType>(val context: Context) {
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
                        errorMessage = context.resources.getString(R.string.ERROR_INTERNET_UNAVAILABLE)
                    )
                )
            } catch (e: Exception) {
                Timber.e("An error happened: $e")
                setValue(
                    Resource.error(
                        e,
                        null,
                        errorMessage = context.resources.getString(R.string.SOMETHING_WENT_WRONG)
                    )
                )
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