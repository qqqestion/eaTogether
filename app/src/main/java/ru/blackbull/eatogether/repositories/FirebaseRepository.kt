package ru.blackbull.eatogether.repositories

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.models.firebase.Notification
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Constants
import ru.blackbull.eatogether.other.Resource
import timber.log.Timber


class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")
    private val notificationsRef = Firebase.firestore.collection("notifications")

    suspend fun searchPartyByPlace(placeId: String): MutableList<Party> {
        val parties = partiesRef
            .whereEqualTo("placeId" , placeId)
            .get()
            .await()
            .toObjects(Party::class.java)
        return parties
    }

    suspend fun addParty(party: Party): Resource<Unit> {
        try {
            partiesRef.add(party).await()
        } catch (e: Throwable) {
            return Resource.error(e)
        }
        return Resource.success(null)
    }

    suspend fun updateParty(party: Party) {
        partiesRef.document(party.id!!).set(party).await()
    }

    suspend fun getPartiesByCurrentUser(): MutableList<Party> {
        return partiesRef
            .whereArrayContains("users" , auth.uid!!)
            .get()
            .await()
            .toObjects(Party::class.java)
    }

    suspend fun getCurrentUser(): User? {
        return getUser(auth.uid!!)
    }

    suspend fun getUser(uid: String): User {
        val user = usersRef
            .document(uid)
            .get()
            .await()
            .toObject(User::class.java)!!
        Timber.d("getUser: $user")
        return user
    }


    fun getCurrentUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

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

    suspend fun signIn(email: String , password: String): Resource<Boolean> {
        val firebaseUser: FirebaseUser?
        try {
            val result = auth
                .signInWithEmailAndPassword(email , password)
                .await()
            firebaseUser = result.user
        } catch (e: FirebaseException) {
            Timber.d(e)
            return Resource.error(e)
        }
        return Resource.success(firebaseUser != null)
    }

    suspend fun signUpWithEmailAndPassword(
        userInfo: User ,
        password: String
    ): Resource<Unit> {
        val firebaseUser: FirebaseUser?
        try {
            val result = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(userInfo.email!! , password)
                .await()
            firebaseUser = result.user
        } catch (e: FirebaseException) {
            Timber.d(e)
            return Resource.error(e)
        }
        userInfo.imageUri = Constants.DEFAULT_IMAGE_URL
        usersRef.document(firebaseUser!!.uid).set(userInfo).await()
        return Resource.success(null)
    }

    suspend fun getNearbyUsers(): MutableList<User> {
        val currentUser = getUser(auth.uid!!)
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
        return users
    }

    suspend fun dislikeUser(user: User) {
        if (user.id == null) {
            return
        }
        val documentRef = usersRef.document(auth.uid!!)
        val document = documentRef.get().await()
        val field = document.get("dislikedUsers")
        if (field != null) {
            val value = field as MutableList<String>
            value.add(user.id!!)
            documentRef.update("dislikedUsers" , value).await()
        } else {
            documentRef.update(
                "dislikedUsers" , mutableListOf(user.id!!)
            ).await()
        }
    }

    suspend fun likeUser(user: User): Boolean {
        val documentRef = usersRef.document(auth.uid!!)
        val document = documentRef.get().await()
        val field = document.get("likedUsers")
        if (field != null) {
            val value = field as MutableList<String>
            user.id?.let { value.add(it) }
            documentRef.update("likedUsers" , value).await()
        } else {
            documentRef.update("likedUsers" , mutableListOf(user.id!!)).await()
        }
        return user.likedUsers.contains(document.id)
    }

    suspend fun getPartyById(id: String): Party? {
        return partiesRef.document(id).get().await().toObject(Party::class.java)
    }

    suspend fun getPartyParticipants(party: Party): MutableList<User> {
        return usersRef.whereIn(
            FieldPath.documentId() ,
            party.users
        ).get().await().toObjects(User::class.java)
    }

    suspend fun addCurrentUserToParty(party: Party) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        if (!party.users.contains(uid)) {
            party.users.add(uid)
            updateParty(party)
        }
    }

    suspend fun sendLikeNotification(user: User) {
        val notification = Notification(userId = user.id , type = "like")
        notificationsRef.add(notification).await()
    }
}
