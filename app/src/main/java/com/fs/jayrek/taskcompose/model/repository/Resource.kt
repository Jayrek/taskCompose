package com.fs.jayrek.taskcompose.model.repository

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

inline fun <T> safeApiCall(apiCall: () -> Resource<T>): Resource<T> {
    return try {
        apiCall.invoke()
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }
}