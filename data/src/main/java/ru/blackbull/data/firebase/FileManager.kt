package ru.blackbull.data.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FileManager @Inject constructor(
    private val storage: FirebaseStorage
) {

    suspend fun upload(uri: Uri): Uri? = storage
        .reference
        .child(UUID.randomUUID().toString())
        .putFile(uri)
        .await()
        .metadata
        ?.reference
        ?.downloadUrl
        ?.await()
}