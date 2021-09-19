package ru.blackbull.domain.usecases

import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.UseCase
import javax.inject.Inject

class IsSignInUseCase @Inject constructor(
    private val authDataSource: AuthDataSource ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<UseCase.None , Boolean>(dispatchers) {

    override suspend fun doWork(params: None) = authDataSource.isSignIn()
}