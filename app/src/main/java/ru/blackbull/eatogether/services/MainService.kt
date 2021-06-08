package ru.blackbull.eatogether.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.models.firebase.Match
import ru.blackbull.eatogether.other.Constants.NOTIFICATION_CHANNEL_ID
import ru.blackbull.eatogether.other.Constants.NOTIFICATION_CHANNEL_NAME
import ru.blackbull.eatogether.other.Constants.NOTIFICATION_ID
import ru.blackbull.eatogether.other.Constants.START_SERVICE
import ru.blackbull.eatogether.other.Constants.STOP_SERVICE
import ru.blackbull.eatogether.other.Constants.TIMER_UPDATE_LOCATION_INTERVAL
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.other.safeCall
import ru.blackbull.eatogether.repositories.FirebaseRepository
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainService : LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var repository: FirebaseRepository

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    companion object {
        var isWorking = true

        val lastLocation = MutableLiveData<Location>()

        /**
         * Используем для отслеживая совпадения пользователей
         */
        val matches = MutableLiveData<Event<Resource<List<Match>>>>()
    }

    override fun onStartCommand(intent: Intent? , flags: Int , startId: Int): Int {
        intent?.action?.also { action ->
            when (action) {
                START_SERVICE -> {
                    startService()
                }
                STOP_SERVICE -> {
                    killService()
                }
            }
        }
        return super.onStartCommand(intent , flags , startId)
    }

    @SuppressLint("MissingPermission")
    private fun startService() {
        Timber.d("Starting service...")
        isWorking = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isWorking) {
                val location = fusedLocationProviderClient.lastLocation.await()
                Timber.d("Location: $location")
                location?.let { notNullLocation ->
                    lastLocation.postValue(notNullLocation)
                    repository.updateUserLocation(notNullLocation)
                }
                delay(TIMER_UPDATE_LOCATION_INTERVAL)
            }
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        // TODO: Переписать на lambda, без firebase импортов
        // TODO: разобраться с этим
//        repository
//            .matchesRef
//            .whereEqualTo("firstLiker" , repository.getCurrentUserId())
//            .whereEqualTo("processed" , false)
//            .addSnapshotListener { value , error ->
//                if (error != null) {
//                    Timber.d(error , "Failed in listener")
//                    matches.postValue(Event(Resource.Error(error)))
//                    return@addSnapshotListener
//                }
//                val resource = safeCall {
//                    val matchesList = mutableListOf<Match>()
//                    value?.documents?.forEach { document ->
//                        matchesList.add(document.toObject(Match::class.java)!!)
//                    }
//                    Timber.d("matches: $matchesList")
//                    Resource.Success(matchesList)
//                }
//                matches.postValue(Event(resource))
//
//                baseNotificationBuilder.setContentText("У вас ${value?.documents?.size} уведомлений")
//                with(NotificationManagerCompat.from(this)) {
//                    notify(NOTIFICATION_ID , baseNotificationBuilder.build())
//                }
//            }
    }

    private fun killService() {
        Timber.d("Stopped service")
        isWorking = false
        stopForeground(true)
        stopSelf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID ,
            NOTIFICATION_CHANNEL_NAME ,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

}