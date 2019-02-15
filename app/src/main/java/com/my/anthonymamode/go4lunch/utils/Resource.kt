package com.my.anthonymamode.go4lunch.utils

open class Resource<out T> {
    class Success<out T>(val data: T) : Resource<T>()
    class Error(val error: Throwable) : Resource<Nothing>()
    class Loading : Resource<Nothing>()
}