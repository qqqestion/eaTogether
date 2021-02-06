package ru.blackbull.eatogether.services

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.other.Constants.START_SERVICE
import ru.blackbull.eatogether.other.Constants.STOP_SERVICE
import ru.blackbull.eatogether.other.Constants.TIMER_UPDATE_LOCATION_INTERVAL
import ru.blackbull.eatogether.repositories.FirebaseRepository
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainService : LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var repository: FirebaseRepository

    companion object {
        var isWorking = true

        val lastLocation = MutableLiveData<Location>()
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
                lastLocation.postValue(location)
                repository.updateUserLocation(location)
                delay(TIMER_UPDATE_LOCATION_INTERVAL)
            }
        }
    }

    private fun killService() {
        Timber.d("Stopped service")
        isWorking = false
        stopForeground(true)
        stopSelf()
    }

}