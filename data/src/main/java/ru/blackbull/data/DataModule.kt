package ru.blackbull.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.UserRepository
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun providePartyRepository(api: FirebaseApi): PartyDataSource = PartyRepository(api)

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
        Firebase.firestore.collection("users")

    @[Provides Singleton]
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @[Provides Singleton]
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
interface BindsModule {

    @Binds
    fun bindUserRepository(implementation: DefaultUserRepository): UserRepository
}

const val USER_COLLECTION_REF = "user-collection-ref"
const val PARTY_COLLECTION_REF = "party-collection-ref"