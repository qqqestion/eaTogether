package ru.blackbull.eatogether.di

import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.Constants

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context
    ) = NotificationCompat.Builder(context , Constants.NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_add)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
}