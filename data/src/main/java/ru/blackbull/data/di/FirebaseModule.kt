package ru.blackbull.data.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import ru.blackbull.data.firebase.FirebaseCollections
import ru.blackbull.domain.AppCoroutineDispatchers
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {

    @Singleton
    @Provides
    fun provideAppCoroutineDispatchers() = AppCoroutineDispatchers(
        Dispatchers.IO,
        Dispatchers.Default,
        Dispatchers.Main,
    )

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences("eatogether_shared_preferences", Context.MODE_PRIVATE)

    @[Singleton Provides Named(USER_COLLECTION_REF)]
    fun provideUserCollectionReference(): CollectionReference =
        Firebase.firestore.collection(FirebaseCollections.USERS.collectionName)

    @[Singleton Provides Named(PARTY_COLLECTION_REF)]
    fun providePartyCollectionReference(): CollectionReference =
        Firebase.firestore.collection(FirebaseCollections.PARTIES.collectionName)

    @[Provides Singleton]
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @[Provides Singleton]
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()
}

const val USER_COLLECTION_REF = "user-collection-ref"
const val PARTY_COLLECTION_REF = "party-collection-ref"