package com.caressa.repository.utils

import timber.log.Timber

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val error: Throwable?,
    val errorMessage: String = "",
    val errorNumber: String = "0"
) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            Timber.i("success=>> $data")
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(
            error: Throwable,
            data: T?,
            errorMessage: String = "",
            errorNumber: String = "0"
        ): Resource<T> {
            Timber.i("error=>> $errorMessage == $errorNumber")
            return Resource(Status.ERROR, data, error, errorMessage, errorNumber)
        }

        fun <T> loading(data: T?): Resource<T> {
            Timber.i("loading=>> $data")
            return Resource(Status.LOADING, data, null)
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }
}