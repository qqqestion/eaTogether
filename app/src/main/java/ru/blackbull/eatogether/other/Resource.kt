package ru.blackbull.eatogether.other

sealed class Resource<out T>(
    val data: T? ,
    val msg: String? ,
    val error: Throwable?
) {

    class Success<T>(
        data: T? = null
    ) : Resource<T>(data = data , msg = null , error = null)

    class Loading<T>(
        data: T? = null
    ) : Resource<T>(data = data , msg = null , error = null)

    class Error<T>(
        error: Throwable? = null ,
        msg: String? = null ,
        data: T? = null
    ) : Resource<T>(data = data , msg = msg , error = error)
}
