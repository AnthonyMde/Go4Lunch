package com.my.anthonymamode.go4lunch.utils

/**
 * Utility class helper used in conjunction with viewmodel and livedata.
 * Used to wrap asynchronous calls into an Resource object which defines a
 * specific state for the livedata (Success, Error or Loading).
 */
open class Resource<out T> {
    class Success<out T>(val data: T) : Resource<T>()
    class Error(val error: Throwable) : Resource<Nothing>()
    class Loading : Resource<Nothing>()
}
