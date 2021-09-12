package ru.blackbull.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.blackbull.domain.functional.Either

abstract class UseCase<in P , out R>(
    private val dispatchers: AppCoroutineDispatchers
) {

    operator fun invoke(
        params: P ,
        scope: CoroutineScope ,
        onResult: (Either<Throwable , R>) -> Unit = {}
    ) {
        // TODO: добавить try/catch для отлова ошибок
        val job = scope.async(dispatchers.io) {
            try {
                Either.Right(doWork(params))
            } catch (e: Throwable) {
                Either.Left(e)
            }
        }
        scope.launch(dispatchers.main) { onResult(job.await()) }
    }

    abstract suspend fun doWork(params: P): R

    object None
}
