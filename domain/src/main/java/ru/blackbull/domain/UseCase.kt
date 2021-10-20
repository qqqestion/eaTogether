package ru.blackbull.domain

import ru.blackbull.domain.functional.Either

abstract class UseCase<in P , out R>(
    private val dispatchers: AppCoroutineDispatchers
) {

    suspend operator fun invoke(
        params: P
    ): Either<Throwable , R> {
        return try {
            Either.Right(doWork(params))
        } catch (e: Throwable) {
            Either.Left(e)
        }
    }

    abstract suspend fun doWork(params: P): R

    object None
}
