package ru.blackbull.eatogether.repositories

import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.blackbull.eatogether.models.firebase.Match
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Constants
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.other.safeCall
import timber.log.Timber


class FirebaseRepository {

    val auth = FirebaseAuth.getInstance()
    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")
    private val notificationsRef = Firebase.firestore.collection("notifications")
    val matchesRef = Firebase.firestore.collection("matches")

    suspend fun updateUserLocation(location: Location): Unit = withContext(Dispatchers.IO) {
        // Используется com.firebase.firestore.GeoPoint потому что в firestore его легче сериализовать
        val geoPoint = GeoPoint(
            location.latitude , location.longitude
        )
        usersRef.document(
            auth.uid!!
        ).update(
            hashMapOf<String , Any>("lastLocation" to geoPoint)
        ).await()
    }

    suspend fun searchPartyByPlace(placeId: String) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(
                partiesRef
                    .whereEqualTo("placeId" , placeId)
                    .get()
                    .await()
                    .toObjects(Party::class.java)
            )
        }
    }

    suspend fun addParty(party: Party) = withContext(Dispatchers.IO) {
        safeCall {
            partiesRef.add(party).await()
            Resource.Success<Unit>()
        }
    }

    suspend fun updateParty(party: Party) = withContext(Dispatchers.IO) {
        partiesRef.document(party.id!!).set(party).await()
    }

    suspend fun getPartiesByCurrentUser() = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(
                partiesRef
                    .whereArrayContains("users" , auth.uid!!)
                    .get()
                    .await()
                    .toObjects(Party::class.java)
            )
        }
    }

    suspend fun getCurrentUser(): Resource<User> = getUser(auth.uid!!)

    suspend fun getUser(uid: String): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(
                usersRef
                    .document(uid)
                    .get()
                    .await()
                    .toObject(User::class.java)
            )
        }
    }

    fun getCurrentUserId(): String = auth.uid!!

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun updateUser(user: User) {
        val firebaseUser = auth.currentUser ?: return
        if (firebaseUser.email != user.email) {
            firebaseUser.updateEmail(user.email!!)
            auth.updateCurrentUser(firebaseUser)
        }

        // Чтобы не фотография из firebase не загружалась повторно в firebase
        // TODO: добавить обновление фотографии
//        if (user._imageUri?.host != "firebasestorage.googleapis.com") {
//            val res = FirebaseStorage.getInstance().reference.child(firebaseUser.uid).putFile(
//                Uri.parse(user.imageUri!!)
//            ).await()
//            val imageUri = res.metadata?.reference?.downloadUrl?.await()
//
//            user.imageUri = imageUri.toString()
//        }

        usersRef.document(auth.uid!!).set(user).await()
    }

    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    suspend fun signIn(email: String , password: String) = withContext(Dispatchers.IO) {
        safeCall {
            val firebaseUser: FirebaseUser?
            val result = auth
                .signInWithEmailAndPassword(email , password)
                .await()
            firebaseUser = result.user
            Resource.Success(firebaseUser != null)
        }
    }

    suspend fun signUpWithEmailAndPassword(
        userInfo: User ,
        password: String
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val firebaseUser: FirebaseUser?
            val result = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(userInfo.email!! , password)
                .await()
            firebaseUser = result.user
            userInfo.imageUri = Constants.DEFAULT_IMAGE_URL
            usersRef.document(firebaseUser!!.uid).set(userInfo).await()
            Resource.Success<Unit>()
        }
    }

    suspend fun getNearbyUsers() = withContext(Dispatchers.IO) {
        safeCall {
            val currentUser = getUser(auth.uid!!).data!!
            // Добавляем в "исключенных" уже лайкнутых, дислайкнутых пользователей
            // и текущего пользователя
            val excludeUsers = currentUser.likedUsers +
                    currentUser.dislikedUsers +
                    currentUser.id
            val queryRef = usersRef.whereNotIn(
                // Ищем по ID документа
                FieldPath.documentId() ,
                excludeUsers
            )

            val users = queryRef.get().await().toObjects(User::class.java)
            Timber.d("users: $users")
            Resource.Success(users)
        }
    }

    suspend fun dislikeUser(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            if (user.id == null) {
                return@withContext
            }
            val curUserRef = usersRef.document(auth.uid!!)
            val curUser = curUserRef.get().await().toObject(User::class.java)
            curUser!!.dislikedUsers += user.id!!
            curUserRef.update("dislikedUsers" , curUser!!.likedUsers).await()
            Resource.Success<Unit>()
        }
    }

    suspend fun likeUser(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            val curUserRef = usersRef.document(auth.uid!!)
            val curUser = curUserRef.get().await().toObject(User::class.java)
            curUser!!.likedUsers += user.id!!
            curUserRef.update("likedUsers" , curUser!!.likedUsers).await()
            // Если другой польватель лайкнул текущего, возвращаем true
            val isLiked = user.likedUsers.contains(curUser.id)
            if (isLiked) {
                val match = Match(firstLiker = user.id , secondLiker = auth.uid)
                matchesRef.add(match).await()
                Resource.Success(user)
            } else {
                Resource.Success(null)
            }
        }
    }

    suspend fun getPartyById(id: String) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(
                partiesRef
                    .document(id)
                    .get()
                    .await()
                    .toObject(Party::class.java)
            )
        }
    }

    suspend fun getPartyParticipants(party: Party) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(
                usersRef
                    .whereIn(FieldPath.documentId() , party.users)
                    .get()
                    .await()
                    .toObjects(User::class.java)
            )
        }
    }

    suspend fun addCurrentUserToParty(party: Party) = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        if (!party.users.contains(uid)) {
            party.users.add(uid)
            updateParty(party)
        }
    }

    suspend fun sendLikeNotification(user: User) {
//        val notification = Notification(userId = user.id , type = "like")
//        notificationsRef.add(notification).await()
    }
}
