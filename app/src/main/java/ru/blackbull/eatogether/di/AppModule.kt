package ru.blackbull.eatogether.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.blackbull.eatogether.api.BaseFirebaseApi
import ru.blackbull.eatogether.api.FirebaseApi
import ru.blackbull.eatogether.repositories.PlaceRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePlaceRepository(
        searchManager: SearchManager
    ) = PlaceRepository(searchManager)

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)
//
//    @Singleton
//    @Provides
//    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.NONE
//    }
//
//    @Singleton
//    @Provides
//    fun provideHttpClient(
//        loggingInterceptor: HttpLoggingInterceptor
//    ) = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .addNetworkInterceptor(loggingInterceptor)
//        .connectTimeout(10 , TimeUnit.SECONDS)
//        .writeTimeout(30 , TimeUnit.SECONDS)
//        .readTimeout(30 , TimeUnit.SECONDS)
//        .build()
//
//    @Singleton
//    @Provides
//    fun provideRetrofitInstance(
//        httpClient: OkHttpClient
//    ): Retrofit = Retrofit.Builder()
//        .baseUrl(Constants.BASE_GOOGLE_API_URL)
//        .client(httpClient)
//        .addConverterFactory(MoshiConverterFactory.create())
//        .build()
//
//    @Singleton
//    @Provides
//    fun provideGooglePlaceApiService(
//        retrofit: Retrofit
//    ): GooglePlaceApiService = retrofit.create(GooglePlaceApiService::class.java)

    @Singleton
    @Provides
    fun provideFirebaseApi() = FirebaseApi() as BaseFirebaseApi

    @Singleton
    @Provides
    fun provideSearchManager() = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.ONLINE
    )
}