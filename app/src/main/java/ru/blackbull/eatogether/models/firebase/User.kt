package ru.blackbull.eatogether.models.firebase

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.io.Serializable


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
) : Parcelable {
    override fun writeToParcel(parcel: Parcel? , flags: Int) {
        TODO("Not yet implemented")
    }

    override fun describeContents(): Int = 0

    companion object {
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel?) = User(
                email = parcel?.readString() ,
                firstName = parcel?.readString() ,
                lastName = parcel?.readString() ,
                description = parcel?.readString() ,
                birthday = parcel?.readParcelable(Timestamp::class.java.classLoader) ,
                _imageUri = parcel?.readParcelable(Uri::class.java.classLoader) ,
                imageUri = parcel?.readString() ,
                likedUsersId = parcel?.createStringArrayList() ?: mutableListOf() ,
                dislikedUsersId = parcel?.createStringArrayList() ?: mutableListOf()
            )

            override fun newArray(size: Int) = Array(size) {
                User()
            }
        }
    }
}