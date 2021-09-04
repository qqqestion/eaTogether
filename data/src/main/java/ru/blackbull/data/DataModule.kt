package ru.blackbull.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun providePartyRepository(api: FirebaseApi): PartyDataSource = PartyRepository(api)

    @Singleton
    @Provides
    fun provideFirebaseRepository(api: FirebaseApi): FirebaseDataSource = FirebaseRepository(api)
}