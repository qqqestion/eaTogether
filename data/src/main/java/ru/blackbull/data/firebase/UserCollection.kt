package ru.blackbull.data.firebase

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import ru.blackbull.data.di.USER_COLLECTION_REF
import ru.blackbull.domain.models.DomainAuthUser
import ru.blackbull.domain.models.firebase.DomainUser
import javax.inject.Inject
import javax.inject.Named

class UserCollection @Inject constructor(
    @Named(USER_COLLECTION_REF) private val collection: CollectionReference
) {

    suspend fun getById(id: String): DomainUser? = collection.document(id)
        .get()
        .await()
        .toObject(DomainUser::class.java)

    suspend fun save(id: String, user: DomainAuthUser) =
        collection.document(id).set(user).await()
}