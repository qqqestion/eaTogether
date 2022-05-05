package ru.blackbull.eatogether.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module()
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