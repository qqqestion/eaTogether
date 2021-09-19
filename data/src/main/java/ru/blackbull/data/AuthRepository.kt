package ru.blackbull.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.models.DomainAuthUser
import ru.blackbull.domain.models.firebase.DomainUser
import javax.inject.Inject

class AuthRepository
@Inject constructor(
    /* Как воткнуть сюда auth и ссылки чтобы тестилось красиво */
) : AuthDataSource {
    private val auth = FirebaseAuth.getInstance()
    private val refUsers = Firebase.firestore.collection("users")

    override suspend fun signInWithEmailAndPassword(email: String , password: String) {
        auth.signInWithEmailAndPassword(email , password)
            .await()
    }

    override suspend fun createAccount(
        email: String ,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email , password).await()
    }

    override fun isSignIn() = auth.uid != null

    override suspend fun isAccountInfoSet(): Boolean {
        if (auth.uid == null) return false
        return refUsers.document(auth.uid!!).get().await().toObject(DomainUser::class.java) == null
    }

    override suspend fun setAccountInfo(user: DomainAuthUser) {
        val firebaseUser = auth.currentUser
        refUsers.document(firebaseUser!!.uid).set(user).await()
    }

    override fun signOut() {
        auth.signOut()
    }
}