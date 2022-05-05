package ru.blackbull.domain.functional

import ru.blackbull.domain.Resource

sealed class Either<out A, out B> {

    data class Left<A>(val error: A) : Either<A, Nothing>()
    data class Right<B>(val value: B) : Either<Nothing, B>()

    fun fold(fnL: (A) -> Unit, fnR: (B) -> Unit) = when (this) {
        is Left -> fnL(error)
        is Right -> fnR(value)
    }

    fun toResource(): Resource<B> {
        return when (this) {
            is Left -> Resource.Error(msg = "")
            is Right -> Resource.Success(value)
        }
    }
}

fun <B> Right(value: B) = Either.Right(value)
fun <A> Left(value: A) = Either.Left(value)

val <A, B> Either<A, B>.value: B?
    get() = when(this) {
        is Either.Left -> null
        is Either.Right -> value
    }

val <A, B> Either<A, B>.error: A?
    get() = when(this) {
        is Either.Left -> error
        is Either.Right -> null
    }

val <A, B> Either<A, B>.isSuccess: Boolean
    get() = this is Either.Right<B>

val <A, B> Either<A, B>.isFailure: Boolean
    get() = this is Either.Left<A>

inline fun <A> runEither(body: () -> A): Either<Throwable, A> = try {
    Either.Right(body())
} catch (e: Throwable) {
    Either.Left(e)
}

inline fun <A, B, C> Either<A, B>.map(fn: (B) -> C): Either<A, C> = when (this) {
    is Either.Left -> Either.Left(error)
    is Either.Right -> Either.Right(fn(value))
}

inline fun <A, B> Either<A, B>.onSuccess(fn: (B) -> Unit): Either<A, B> = this.apply {
    if (this is Either.Right) fn(value)
}

inline fun <A, B> Either<A, B>.onFailure(fn: (A) -> Unit): Either<A, B> = this.apply {
    if (this is Either.Left) fn(error)
}

inline fun <A, B, C> Either<A, B>.mapFailure(fn: (A) -> C): Either<C, B> = when (this) {
    is Either.Left -> Either.Left(fn(error))
    is Either.Right -> Either.Right(value)
}
