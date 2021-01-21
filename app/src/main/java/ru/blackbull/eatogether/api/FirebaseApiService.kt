package ru.blackbull.eatogether.api

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.models.firebase.Notification
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Resource


class FirebaseApiService {
    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")
    private val notificationsRef = Firebase.firestore.collection("notifications")

    suspend fun searchPartyByPlace(placeId: String): MutableList<Party> {
        val parties = mutableListOf<Party>()

        val documents = partiesRef.whereEqualTo("placeId" , placeId).get().await()

        for (document in documents) {
            val party = document.toObject(Party::class.java)
            parties.add(party)
        }
        return parties
    }

    suspend fun getPartiesByCurrentUser(): MutableList<Party> {
        val user = getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
        val parties = mutableListOf<Party>()
        val documents = partiesRef.whereArrayContains("users" , user.id!!).get().await()
        documents.forEach { document ->
            val party = document.toObject(Party::class.java)
            parties.add(party)
        }
        return parties
    }

    suspend fun addParty(party: Party): Resource<Unit> {
        try {
            partiesRef.add(party).await()
        } catch (e: Throwable) {
            return Resource.error(e , null , null)
        }
        return Resource.success(null)
    }

    suspend fun getUser(uid: String): User {
        val document = usersRef.document(uid).get().await()
        val user = document.toObject(User::class.java)!!
        val str = document.getString("imageUri") ?: ""
        user._imageUri = Uri.parse(str)
        Log.d("ImageDebug" , "getCurrentUser: $user")
        return user
    }

    suspend fun updateUser(user: User) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        if (firebaseUser.email != user.email) {
            firebaseUser.updateEmail(user.email!!)
            FirebaseAuth.getInstance().updateCurrentUser(firebaseUser)
        }
        Log.d("ImageDebug" , "updateUser: $user")

        // Чтобы не фотография из firebase не загружалась повторно в firebase
        if (user._imageUri?.host != "firebasestorage.googleapis.com") {
            val res = FirebaseStorage.getInstance().reference.child(firebaseUser.uid).putFile(
                user._imageUri!!
            ).await()
            val imageUri = res.metadata?.reference?.downloadUrl?.await()

            user._imageUri = imageUri
            user.imageUri = user._imageUri.toString()
        }

        getCurrentUserRef().set(user)
            .addOnSuccessListener {
                Log.d("EditProfile" , "updateUser: success")
            }
            .addOnFailureListener { e ->
                Log.d("EditProfile" , "updateUser: failed" , e)
            }
        Log.d("EditProfile" , "uri host: ${user._imageUri?.host}")
    }

    suspend fun addUser(uid: String , userInfo: User) {
        val uriResult = FirebaseStorage.getInstance()
            .reference
            .child("download.jpeg")
            .downloadUrl.await()
        userInfo.imageUri = uriResult.toString()
        Log.d("FirebaseAuthDebug" , "default image uri: ${userInfo.imageUri}")
        usersRef.document(uid).set(userInfo).await()
    }

    suspend fun getNearbyUsers(): MutableList<User> {
        val users = mutableSetOf<User>()
        val currentUser = getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
        val excludeUsers = currentUser.likedUsersId + currentUser.dislikedUsersId
        val queryRef = when {
            excludeUsers.isEmpty() -> {
                usersRef
            }
            else -> {
                // Ищем по id документа
                usersRef.whereNotIn(
                    FieldPath.documentId() ,
                    excludeUsers
                )
            }
        }
        val documents = queryRef.get().await()
        for (document in documents) {
            if (document.id != currentUser.id) {
                val user = document.toObject(User::class.java)
                user._imageUri = Uri.parse(user.imageUri)
                users.add(user)
            }
        }
        Log.d("NearbyDebug" , "users: ${users.toMutableList()}")
        return users.toMutableList()
    }

    suspend fun likeUser(user: User): Boolean {
        val documentRef = getCurrentUserRef()
        val document = documentRef.get().await()
        val field = document.get("likedUsers")
        if (field != null) {
            val value = field as MutableList<String>
            user.id?.let { value.add(it) }
            documentRef.update("likedUsers" , value).await()
        } else {
            documentRef.update("likedUsers" , mutableListOf(user.id!!)).await()
        }
        return user.likedUsersId.contains(document.id)
    }

    suspend fun dislikeUser(user: User) {
        val documentRef = getCurrentUserRef()
        val document = documentRef.get().await()
        val field = document.get("dislikedUsers")
        if (field != null) {
            val value = field as MutableList<String>
            user.id?.let { value.add(it) }
            documentRef.update("dislikedUsers" , value).await()
        } else {
            documentRef.update("dislikedUsers" , mutableListOf(user.id!!)).await()
        }

    }

    private fun getCurrentUserRef(): DocumentReference {
        return usersRef.document(
            FirebaseAuth.getInstance().currentUser?.uid!!
        )
    }

    suspend fun getPartyById(id: String): Party? {
        val document = partiesRef.document(id).get().await()
        return document.toObject(Party::class.java)
    }

    suspend fun getPartyParticipants(party: Party): MutableList<User> {
        val documents = usersRef.whereIn(
            FieldPath.documentId() ,
            party.users
        ).get().await()
        val users = mutableListOf<User>()
        for (document in documents) {
            val user = document.toObject(User::class.java)
            user._imageUri = Uri.parse(user.imageUri)
            users.add(user)
        }
        return users
    }

    suspend fun updateParty(party: Party) {
        partiesRef.document(party.id!!).set(party).await()
    }

    suspend fun sendLikeNotification(user: User) {
        val notification = Notification(userId = user.id , type = "like")
        notificationsRef.add(notification).addOnSuccessListener {
            Log.d(
                "NotificationsDebug" ,
                "added new notification userId: ${notification.userId} type: ${notification.type}"
            )
        }
    }
}
