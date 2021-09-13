package ru.blackbull.domain.functional

import ru.blackbull.domain.Resource


sealed class Either<out A , out B> {

    data class Left<A>(val a: A) : Either<A , Nothing>()
    data class Right<B>(val b: B) : Either<Nothing , B>()

    fun fold(fnL: (A) -> Unit , fnR: (B) -> Unit) = when (this) {
        is Left -> fnL(a)
        is Right -> fnR(b)
    }

    fun toResource(): Resource<B> {
        return when (this) {
            is Left -> Resource.Error(msg = "")
            is Right -> Resource.Success(b)
        }
    }

    fun <C> map(fn: (B) -> C): Either<A , C> = when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> Either.Right(fn(b))
    }

    fun onSuccess(fn: (B) -> Unit): Either<A , B> = this.apply {
        if (this is Right) fn(b)
    }

    fun onFailure(fn: (A) -> Unit): Either<A , B> = this.apply {
        if (this is Left) fn(a)
    }
}

//fun <A , B , C> Either<A , B>.map(fn: (B) -> C): Either<A , C> = when (this) {
//    is Either.Left -> Either.Left(a)
//    is Either.Right -> Either.Right(fn(b))
//}
