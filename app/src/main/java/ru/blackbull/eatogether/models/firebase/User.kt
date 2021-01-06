package ru.blackbull.eatogether.models.firebase

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName


data class User(
    @DocumentId
    var id: String? = null ,
    var email: String? = null ,
    var firstName: String? = null ,
    var lastName: String? = null ,
    var description: String? = null ,
    var birthday: Timestamp? = null ,
    @get:Exclude
    var _imageUri: Uri? = null ,
    // Для спокойной сериализации пользователя, потому что uri не хочет сериализироваться
    var imageUri: String? = _imageUri.toString() ,
    @get:PropertyName("likedUsers")
    @set:PropertyName("likedUsers")
    var likedUsersId: MutableList<String> = mutableListOf() ,
    @get:PropertyName("dislikedUsers")
    @set:PropertyName("dislikedUsers")
    var dislikedUsersId: MutableList<String> = mutableListOf()
)