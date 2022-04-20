package ru.blackbull.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.functional.map
import ru.blackbull.domain.functional.mapFailure
import ru.blackbull.domain.functional.runEither
import ru.blackbull.domain.models.DomainAuthUser
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.usecases.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    /* Как воткнуть сюда auth и ссылки чтобы тестилось красиво */
    private val dispatchers: AppCoroutineDispatchers,
) : AuthDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val refUsers = Firebase.firestore.collection("users")

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Either<SignInError, Unit> = runEither {
        auth.signInWithEmailAndPassword(email, password)
            .await()
        Unit
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
        auth.createUserWithEmailAndPassword(email, password).await()
        Unit
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
        if (auth.uid == null) Either.Right(false)
        return isAccountInfoSet()
    }

    override suspend fun isAccountInfoSet(): Either<NetworkError, Boolean> {
        if (auth.uid == null) return Either.Right(false)
        return withContext(dispatchers.io) { getCurrentUser().map { it != null } }
    }

    private suspend fun getCurrentUser(): Either<NetworkError, DomainUser?> {
        return runEither {
            refUsers
                .document(auth.uid!!)
                .get()
                .await()
                .toObject(DomainUser::class.java)
        }.mapFailure { exception ->
            when (exception) {
                is FirebaseFirestoreException -> NoInternetError
                else -> UnexpectedNetworkCommunicationError
            }
        }
    }

    override suspend fun setAccountInfo(user: DomainAuthUser) {
        val firebaseUser = auth.currentUser
        refUsers.document(firebaseUser!!.uid).set(user).await()
    }

    override fun signOut() {
        auth.signOut()
    }
}