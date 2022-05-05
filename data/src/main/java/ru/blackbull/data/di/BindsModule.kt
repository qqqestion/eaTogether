package ru.blackbull.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.blackbull.data.DefaultAuthRepository
import ru.blackbull.data.DefaultMapRepository
import ru.blackbull.data.DefaultPartyRepository
import ru.blackbull.data.DefaultUserRepository
import ru.blackbull.domain.AuthRepository
import ru.blackbull.domain.MapRepository
import ru.blackbull.domain.PartyRepository
import ru.blackbull.domain.UserRepository

@Module
@InstallIn(SingletonComponent::class)
interface BindsModule {

    @Binds
    fun bindUserRepository(implementation: DefaultUserRepository): UserRepository

    @Binds
    fun bindPartyRepository(implementation: DefaultPartyRepository): PartyRepository

    @Binds
    fun bindMapRepository(implementation: DefaultMapRepository): MapRepository

    @Binds
    fun bindAuthDataSource(authRepository: DefaultAuthRepository): AuthRepository
}