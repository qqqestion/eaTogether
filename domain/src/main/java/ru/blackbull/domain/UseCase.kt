package ru.blackbull.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class UseCase<in P , out R>(
    private val dispatchers: AppCoroutineDispatchers
) {

    operator fun invoke(
        params: P ,
        scope: CoroutineScope ,
        onResult: (R) -> Unit = {}
    ) {
        val job = scope.async(dispatchers.io) { doWork(params) }
        scope.launch(dispatchers.main) { onResult(job.await()) }
    }

    abstract suspend fun doWork(params: P): R
}
