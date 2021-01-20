package ru.blackbull.eatogether.other

data class Resource<out T>(
    val status: Status ,
    val data: T? ,
    val msg: String? ,
    val error: Throwable?
) {

    companion object {
        fun <T> success(data: T?) = Resource(Status.SUCCESS , data , null , null)

        fun <T> error(error: Throwable? , msg: String? , data: T?) = Resource(
            Status.ERROR , data , msg , error
        )

        fun <T> loading(data: T?) = Resource(Status.LOADING , data , null , null)
    }
}

enum class Status {
    SUCCESS , ERROR , LOADING
}