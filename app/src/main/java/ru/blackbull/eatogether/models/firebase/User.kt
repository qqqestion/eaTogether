package ru.blackbull.eatogether.models.firebase

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint


data class User(
    @DocumentId
    var id: String? = null ,
    var email: String? = null ,
    var firstName: String? = null ,
    var lastName: String? = null ,
    var description: String? = null ,
    var birthday: Timestamp? = null ,
    var imageUri: String? = null ,
    var likedUsers: List<String> = mutableListOf() ,
    var dislikedUsers: List<String> = mutableListOf() ,
    var lastLocation: GeoPoint? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readParcelable(Timestamp::class.java.classLoader) ,
        parcel.readString() ,
        parcel.createStringArrayList() ?: listOf() ,
        parcel.createStringArrayList() ?: listOf()
    ) {
        val latitude = parcel.readDouble()
        val longitude = parcel.readDouble()
        lastLocation = GeoPoint(latitude , longitude)
    }

    override fun writeToParcel(parcel: Parcel? , flags: Int) {
        parcel?.let {
            it.writeString(id)
            it.writeString(email)
            it.writeString(firstName)
            it.writeString(lastName)
            it.writeString(description)
            it.writeParcelable(birthday , flags)
            it.writeString(imageUri)
            it.writeList(likedUsers)
            it.writeList(dislikedUsers)
            it.writeDouble(lastLocation!!.latitude)
            it.writeDouble(lastLocation!!.longitude)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}