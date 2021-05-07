package ru.blackbull.eatogether.repositories

import android.location.Location
import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.blackbull.eatogether.api.BaseFirebaseApi
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.other.safeCall
import javax.inject.Inject


class FirebaseRepository @Inject constructor(
    private val firebaseApi: BaseFirebaseApi
) {

    val matchesRef = Firebase.firestore.collection("matches")

    suspend fun updateUserLocation(location: Location): Unit = withContext(Dispatchers.IO) {
        firebaseApi.updateUserLocation(location)
    }

    suspend fun searchPartyByPlace(
        placeId: String
    ): Resource<List<Party>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.searchPartyByPlace(placeId))
        }
    }

    suspend fun addParty(party: Party): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            firebaseApi.addParty(party)
            Resource.Success()
        }
    }

    suspend fun updateParty(party: Party) = withContext(Dispatchers.IO) {
        firebaseApi.updateParty(party)
    }

    suspend fun getPartiesByCurrentUser(): Resource<List<Party>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getPartiesByCurrentUser())
        }
    }

    suspend fun getCurrentUser(): Resource<User> = getUser(getCurrentUserId())

    suspend fun getUser(uid: String): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getUser(uid))
        }
    }

    fun getCurrentUserId(): String = firebaseApi.getCurrentUserId()

    fun signOut() {
        firebaseApi.signOut()
    }

    suspend fun updateUser(user: User , photoUri: Uri) {
        firebaseApi.updateUser(user , photoUri)
    }

    fun isAuthenticated(): Boolean {
        return firebaseApi.isAuthenticated()
    }

    suspend fun signIn(email: String , password: String) = withContext(Dispatchers.IO) {
        safeCall {
            firebaseApi.signIn(email , password)
            Resource.Success(Unit)
        }
    }

    suspend fun signUpWithEmailAndPassword(
        user: User ,
        password: String
    ) = withContext(Dispatchers.IO) {
        safeCall {
            firebaseApi.signUpWithEmailAndPassword(user , password)
            Resource.Success<Unit>()
        }
    }

    suspend fun getNearbyUsers(): Resource<MutableList<User>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getNearbyUsers().toMutableList())
        }
    }

    suspend fun dislikeUser(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            firebaseApi.dislikeUser(user)
            Resource.Success<Unit>()
        }
    }

    suspend fun likeUser(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.likeUser(user))
        }
    }

    suspend fun getPartyById(partyId: String): Resource<Party> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getPartyById(partyId))
        }
    }

    suspend fun getPartyParticipants(
        party: Party
    ): Resource<List<User>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getPartyParticipants(party))
        }
    }

    suspend fun addCurrentUserToParty(party: Party) = withContext(Dispatchers.IO) {
        firebaseApi.addCurrentUserToParty(party)
    }

    suspend fun sendLikeNotification(user: User) {
//        val notification = Notification(userId = user.id , type = "like")
//        notificationsRef.add(notification).await()
    }
}
