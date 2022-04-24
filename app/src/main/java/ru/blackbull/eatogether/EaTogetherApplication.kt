package ru.blackbull.eatogether

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class EaTogetherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
    }
}