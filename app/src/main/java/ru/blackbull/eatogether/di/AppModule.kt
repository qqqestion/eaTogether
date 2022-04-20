package ru.blackbull.eatogether.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.blackbull.data.AuthRepository
import ru.blackbull.domain.AuthDataSource
import javax.inject.Singleton

@Module(includes = [BindsModule::class])
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @Singleton
    @Provides
    fun provideSearchManager() = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.ONLINE
    )
}

@Module
@InstallIn(SingletonComponent::class)
interface BindsModule {

    @Binds
    fun bindAuthDataSource(authRepository: AuthRepository): AuthDataSource
}