package ru.blackbull.eatogether.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.blackbull.eatogether.api.GooglePlaceApiService
import ru.blackbull.eatogether.other.Constants
import ru.blackbull.eatogether.repositories.FirebaseRepository
import ru.blackbull.eatogether.repositories.PlaceRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseRepository() = FirebaseRepository()

    @Singleton
    @Provides
    fun providePlaceRepository(
        googlePlaceApiService: GooglePlaceApiService
    ) = PlaceRepository(googlePlaceApiService)

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @Singleton
    @Provides
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addNetworkInterceptor(loggingInterceptor)
        .connectTimeout(10 , TimeUnit.SECONDS)
        .writeTimeout(30 , TimeUnit.SECONDS)
        .readTimeout(30 , TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        httpClient: OkHttpClient
    ) = Retrofit.Builder()
        .baseUrl(Constants.BASE_GOOGLE_API_URL)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideGooglePlaceApiService(
        retrofit: Retrofit
    ) = retrofit.create(GooglePlaceApiService::class.java)
}