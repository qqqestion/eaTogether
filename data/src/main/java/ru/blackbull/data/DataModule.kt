package ru.blackbull.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.FirebaseDataSource
import ru.blackbull.domain.PartyDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {
//
//    @Singleton
//    @Provides
//    fun provideFirebaseApi() = FirebaseApi()

    @Singleton
    @Provides
    fun provideAuthRepository(): AuthDataSource = AuthRepository()

    @Singleton
    @Provides
    fun providePartyRepository(): PartyDataSource = PartyRepository()

    @Singleton
    @Provides
    fun provideFirebaseRepository(api: FirebaseApi): FirebaseDataSource = FirebaseRepository(api)

    @Singleton
    @Provides
    fun provideAppCoroutineDispatchers() = AppCoroutineDispatchers(
        Dispatchers.IO,
        Dispatchers.Default,
        Dispatchers.Main,
    )
}