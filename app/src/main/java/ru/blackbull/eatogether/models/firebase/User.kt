package ru.blackbull.eatogether.models.firebase

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint

/**
 * Класс, описывающий пользователя
 *
 * @property id
 * @property phone
 * @property firstName
 * @property lastName
 * @property description
 * @property birthday
 * @property mainImageUri
 * @property likedUsers
 * @property dislikedUsers
 * @property lastLocation
 */
data class User(
    @DocumentId
    var id: String? = null ,
    @Exclude
    var phone: String? = null ,
    var firstName: String? = null ,
    var lastName: String? = null ,
    var description: String? = null ,
    var birthday: Timestamp? = null ,
    @field:JvmField
    var isRegistrationComplete: Boolean = true ,
    var mainImageUri: String? = null ,
    var images: List<String> = listOf() ,
    var likedUsers: List<String> = mutableListOf() ,
    var dislikedUsers: List<String> = mutableListOf() ,
    var friendList: List<String> = mutableListOf() ,
    var lastLocation: GeoPoint? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readString() ,
        parcel.readParcelable(Timestamp::class.java.classLoader) ,
        parcel.readInt() != 0 ,
        parcel.readString() ,
        parcel.createStringArrayList() ?: listOf() ,
        parcel.createStringArrayList() ?: listOf() ,
        parcel.createStringArrayList() ?: listOf() ,
        parcel.createStringArrayList() ?: listOf()
    ) {
        val latitude = parcel.readDouble()
        val longitude = parcel.readDouble()
        lastLocation = GeoPoint(latitude , longitude)
    }

    fun fullName(): String {
        return "$firstName $lastName"
    }

    override fun writeToParcel(parcel: Parcel? , flags: Int) {
        parcel?.let {
            it.writeString(id)
            it.writeString(phone)
            it.writeString(firstName)
            it.writeString(lastName)
            it.writeString(description)
            it.writeParcelable(birthday , flags)
            it.writeInt(if (isRegistrationComplete) 1 else 0)
            it.writeString(mainImageUri)
            it.writeList(images)
            it.writeList(likedUsers)
            it.writeList(dislikedUsers)
            it.writeList(friendList)
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

enum class FriendState {
    UNFRIEND ,
    INVITATION_SENT ,
    FRIEND,
    ITSELF
}
