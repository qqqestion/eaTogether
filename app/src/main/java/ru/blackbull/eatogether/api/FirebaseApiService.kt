package ru.blackbull.eatogether.api

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.models.firebase.NewParty
import ru.blackbull.eatogether.models.firebase.NewUser


class FirebaseApiService {
    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")

    suspend fun searchPartyByPlace(placeId: String): MutableList<NewParty> {
        val parties = mutableListOf<NewParty>()

        val documents = partiesRef.whereEqualTo("placeId" , placeId).get().await()

        for (document in documents) {
            val party = document.toObject(NewParty::class.java)
            party.id = document.id
            parties.add(party)
        }
        return parties
    }

    fun addParty(party: NewParty) {
        partiesRef.add(party)
    }

    suspend fun getCurrentUser(): NewUser {
        val document = usersRef.document(
            FirebaseAuth.getInstance().currentUser?.uid!!
        ).get().await()
        val user = document.toObject(NewUser::class.java)!!
        user.id = document.id
        val str = document.getString("imageUri") ?: ""
        user.imageUri = Uri.parse(str)
        Log.d(
            "EditProfile" ,
            "left: ${user.imageUri}"
        )

        return user
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun updateUser(user: NewUser) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        if (firebaseUser.email != user.email) {
            firebaseUser.updateEmail(user.email!!)
            FirebaseAuth.getInstance().updateCurrentUser(firebaseUser)
        }
        Log.d("ImageDebug" , "updateUser: $user")
        usersRef.document(firebaseUser.uid).set(user)
            .addOnSuccessListener {
                Log.d("EditProfile" , "updateUser: success")
            }
            .addOnFailureListener {
                Log.d("EditProfile" , "updateUser: failed")
            }
        val res = FirebaseStorage.getInstance().reference.child(firebaseUser.uid).putFile(
            user.imageUri!!
        ).await()

        val imageUri = res.metadata?.reference?.downloadUrl?.await()

        // Чтобы у firebase не было проблем с сериализацией uri, иначе есть :/
        usersRef.document(firebaseUser.uid).update(
            mapOf(
                "imageUri" to imageUri.toString()
            )
        )
        user.imageUri = imageUri

    }

    fun isAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    suspend fun getCurrentUserPhotoUri(): Uri {
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        return FirebaseStorage.getInstance().reference.child(uid).downloadUrl.await()
    }
}
