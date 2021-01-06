package ru.blackbull.eatogether.api

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User


class FirebaseApiService {
    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")

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
        val user = getCurrentUser()
        val parties = mutableListOf<Party>()
        val documents = partiesRef.whereArrayContains("users",user.id!!).get().await()
        documents.forEach { document ->
            val party = document.toObject(Party::class.java)
            parties.add(party)
        }
        return parties
    }

    fun addParty(party: Party) {
        partiesRef.add(party)
            .addOnSuccessListener {
                Log.d("PartyDebug" , "party $party added successfully")
            }
            .addOnFailureListener { e ->
                Log.d("PartyDebug" , "cannot add party: $party" , e)
            }
    }

    suspend fun getCurrentUser(): User {
        val document = usersRef.document(
            FirebaseAuth.getInstance().currentUser?.uid!!
        ).get().await()
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

        usersRef.document(firebaseUser.uid).set(user)
            .addOnSuccessListener {
                Log.d("EditProfile" , "updateUser: success")
            }
            .addOnFailureListener {
                Log.d("EditProfile" , "updateUser: failed")
            }
        Log.d("EditProfile" , "uri host: ${user._imageUri?.host}")
    }

    suspend fun getCurrentUserPhotoUri(): Uri {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return FirebaseStorage.getInstance().reference.child(uid).downloadUrl.await()
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
}
