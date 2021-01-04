package ru.blackbull.eatogether.models.firebase

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude


data class NewUser(
    @get:Exclude
    var id: String? = null ,
    var email: String? = null ,
    var firstName: String? = null ,
    var lastName: String? = null ,
    var description: String? = null ,
    var birthday: Timestamp? = null ,
    @get:Exclude
    var imageUri: Uri? = null
) {
}