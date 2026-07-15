package com.adentweets.app.core.util

sealed class Resource<out T> {
    data class Loading<T>(val data: T? = null, val message: String? = null) : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val errorCode: Int? = null, val data: T? = null) : Resource<T>()

    val isLoading get() = this is Loading
    val isSuccess get() = this is Success
    val isError get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> data
        is Loading -> data
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw AdenTweetException(message, errorCode)
        is Loading -> throw AdenTweetException("Data is still loading")
    }
}

class AdenTweetException(
    override val message: String,
    val errorCode: Int? = null
) : Exception(message)

// Error codes
object ErrorCodes {
    const val NETWORK_ERROR = 1001
    const val AUTH_INVALID_CREDENTIALS = 2001
    const val AUTH_USER_NOT_FOUND = 2002
    const val AUTH_EMAIL_ALREADY_EXISTS = 2003
    const val AUTH_WEAK_PASSWORD = 2004
    const val AUTH_TOO_MANY_REQUESTS = 2005
    const val AUTH_INVALID_EMAIL = 2006
    const val POST_NOT_FOUND = 3001
    const val POST_DELETE_FAILED = 3002
    const val POST_CREATE_FAILED = 3003
    const val USER_NOT_FOUND = 4001
    const val USER_ALREADY_FOLLOWING = 4002
    const val USER_BLOCKED = 4003
    const val MEDIA_TOO_LARGE = 5001
    const val MEDIA_COMPRESS_FAILED = 5002
    const val MEDIA_DECODE_FAILED = 5003
    const val PERMISSION_DENIED = 6001
    const val UNKNOWN = 9999
}