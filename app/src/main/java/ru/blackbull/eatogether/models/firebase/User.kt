package ru.blackbull.eatogether.models.firebase

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


data class User(
    @DocumentId
    var id: String? = null ,
    var email: String? = null ,
    var firstName: String? = null ,
    var lastName: String? = null ,
    var description: String? = null ,
    var birthday: Timestamp? = null ,
    var imageUri: String? = null ,
    var likedUsers: MutableList<String> = mutableListOf() ,
    var dislikedUsers: MutableList<String> = mutableListOf()
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
                imageUri = parcel?.readString() ,
                likedUsers = parcel?.createStringArrayList() ?: mutableListOf() ,
                dislikedUsers = parcel?.createStringArrayList() ?: mutableListOf()
            )

            override fun newArray(size: Int) = Array(size) {
                User()
            }
        }
    }
}