package ru.blackbull.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.withContext
import ru.blackbull.data.firebase.AuthManager
import ru.blackbull.data.firebase.UserCollection
import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthRepository
import ru.blackbull.domain.functional.*
import ru.blackbull.domain.models.DomainAuthUser
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.usecases.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthRepository @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource,
    private val userCollection: UserCollection,
    private val authManager: AuthManager
) : AuthRepository {

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Either<SignInError, Unit> = runEither {
        authManager.signInWithEmailAndPassword(email, password)
    }.mapFailure { exception ->
        when (exception) {
            is FirebaseAuthInvalidUserException,
            is FirebaseAuthInvalidCredentialsException -> InvalidCredentials
            is FirebaseNetworkException -> NoInternetError
            else -> throw RuntimeException("Error during sign in", exception)
        }
    }

    override suspend fun createAccount(
        email: String,
        password: String,
    ): Either<SignUpError, Unit> = runEither {
        authManager.createUserWithEmailAndPassword(email, password)
    }.mapFailure { exception ->
        when (exception) {
            is FirebaseAuthWeakPasswordException -> WeakPasswordError
            is FirebaseAuthInvalidCredentialsException -> EmailMalformedError
            is FirebaseAuthUserCollisionException -> UserAlreadyExists
            is FirebaseNetworkException -> NoInternetError
            else -> throw RuntimeException("Error during sign up", exception)
        }
    }

    override suspend fun checkAuthenticated(): Either<NetworkError, Boolean> {
        if (authManager.uid == null) Either.Right(false)
        return isAccountInfoSet()
    }

    override suspend fun isAccountInfoSet(): Either<NetworkError, Boolean> {
        if (authManager.uid == null) return Either.Right(false)
        if (sharedPreferencesDataSource.read(Preference.RegistrationComplete))
            return Either.Right(true)
        return withContext(dispatchers.io) {
            getCurrentUser().map { it != null }
        }.onSuccess { isComplete ->
            sharedPreferencesDataSource.write(Preference.RegistrationComplete, isComplete)
        }
    }

    private suspend fun getCurrentUser(): Either<NetworkError, DomainUser?> {
        return runEither {
            userCollection.getById(checkNotNull(authManager.uid))
        }.mapFailure { exception ->
            when (exception) {
                is FirebaseFirestoreException -> NoInternetError
                else -> UnexpectedNetworkCommunicationError
            }
        }
    }

    override suspend fun completeRegistration(user: DomainAuthUser): Either<NetworkError, Unit> =
        runEither {
            userCollection.save(checkNotNull(authManager.uid), user)
            sharedPreferencesDataSource.write(Preference.RegistrationComplete, true)
        }.mapFailure { exception ->
            when (exception) {
                is FirebaseFirestoreException -> NoInternetError
                else -> UnexpectedNetworkCommunicationError
            }
        }

    override fun signOut() = runEither {
        authManager.signOut()

        with(sharedPreferencesDataSource) {
            remove(Preference.RegistrationComplete)
        }
    }.mapFailure { exception ->
        when (exception) {
            is FirebaseFirestoreException -> NoInternetError
            else -> UnexpectedNetworkCommunicationError
        }
    }
}