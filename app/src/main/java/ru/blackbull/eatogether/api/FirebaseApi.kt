package ru.blackbull.eatogether.api

import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import ru.blackbull.eatogether.models.firebase.Match
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Constants
import ru.blackbull.eatogether.other.Resource
import timber.log.Timber

class FirebaseApi : BaseFirebaseApi {

    val auth = FirebaseAuth.getInstance()
    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")
    private val notificationsRef = Firebase.firestore.collection("notifications")
    val matchesRef = Firebase.firestore.collection("matches")

    /**
     * Updates user last location
     *
     * @param location
     */
    override suspend fun updateUserLocation(location: Location) {
        val geoPoint = GeoPoint(
            location.latitude , location.longitude
        )
        usersRef.document(
            auth.uid!!
        ).update(
            "lastLocation" , geoPoint
        ).await()
    }

    /**
     * Searching in all parties and filtering only those that have placeId field the same as given
     *
     * @param placeId place id in Google Places API
     * @return List<Party>
     */
    override suspend fun searchPartyByPlace(
        placeId: String
    ): List<Party> {
        return partiesRef
            .whereEqualTo("placeId" , placeId)
            .get()
            .await()
            .toObjects(Party::class.java)
    }

    /**
     * Creates party
     *
     * @param party
     */
    override suspend fun addParty(party: Party) {
        partiesRef.add(party).await()
    }

    /**
     * Gets party in which current user is member
     *
     * @return
     */
    override suspend fun getPartiesByCurrentUser(): List<Party> {
        return partiesRef
            .whereArrayContains("users" , auth.uid!!)
            .get()
            .await()
            .toObjects(Party::class.java)
    }

    /**
     * Gets user with given id
     *
     * @param uid
     * @return
     */
    override suspend fun getUser(uid: String): User {
        return usersRef
            .document(uid)
            .get()
            .await()
            .toObject(User::class.java) ?: throw Exception("No user with given id $uid")
    }

    /**
     * Updates party
     *
     * @param party
     */
    override suspend fun updateParty(party: Party) {
        partiesRef.document(party.id!!).set(party).await()
    }

    /**
     * Signs out
     *
     */
    override fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    override suspend fun updateUser(user: User) {
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

    override suspend fun signIn(email: String , password: String) {
        auth
            .signInWithEmailAndPassword(email , password)
            .await()
        Resource.Success(Unit)
    }

    /**
     * Firstly we create user account with given email and password, then save all other information
     *
     * @param user where all user info stores, except for password
     * @param password
     */
    override suspend fun signUpWithEmailAndPassword(
        user: User ,
        password: String
    ) {
        val firebaseUser: FirebaseUser?
        val result = FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(user.email!! , password)
            .await()
        firebaseUser = result.user
        user.imageUri = Constants.DEFAULT_IMAGE_URL
        usersRef.document(firebaseUser!!.uid).set(user).await()
    }

    /**
     * Gets nearby user
     * Right now there is no any location and "nearby@ check, so user basically gets all non-liked and non-disliked users
     *
     * @return
     */
    override suspend fun getNearbyUsers(): List<User> {
        val currentUser = getUser(auth.uid!!)

        /**
         * Generates list of users to exclude, so that it won't appear in result set
         */
        val excludeUsers = currentUser.likedUsers +
                currentUser.dislikedUsers +
                currentUser.id
        val queryRef = usersRef.whereNotIn(
            /**
             * Comparing Document ID
             */
            FieldPath.documentId() ,
            excludeUsers
        )

        val users = queryRef.get().await().toObjects(User::class.java)
        Timber.d("users: $users")
        return users
    }

    override suspend fun dislikeUser(user: User) {
        val curUserRef = usersRef.document(auth.uid!!)
        val curUser = curUserRef.get().await().toObject(User::class.java)
        curUser!!.dislikedUsers += user.id!!
        curUserRef.update("dislikedUsers" , curUser!!.dislikedUsers).await()
    }

    /**
     * If users like each other, creates match object, adds it to firestore, and returs given user
     *
     * @param user
     * @return
     */
    override suspend fun likeUser(user: User): User? {
        val curUserRef = usersRef.document(auth.uid!!)
        val curUser = curUserRef.get().await().toObject(User::class.java)

        curUser!!.likedUsers += user.id!!
        curUserRef.update("likedUsers" , curUser!!.likedUsers).await()

        val isLiked = user.likedUsers.contains(curUser.id)
        return if (isLiked) {
            val match = Match(firstLiker = user.id , secondLiker = auth.uid)
            matchesRef.add(match).await()
            user
        } else {
            null
        }
    }

    override suspend fun getPartyById(partyId: String): Party {
        return partiesRef
            .document(partyId)
            .get()
            .await()
            .toObject(Party::class.java) ?: throw Exception("No party with given id: $partyId")
    }

    override suspend fun getPartyParticipants(party: Party): List<User> {
        return usersRef
            .whereIn(FieldPath.documentId() , party.users)
            .get()
            .await()
            .toObjects(User::class.java)
    }

    override suspend fun addCurrentUserToParty(party: Party) {
        val uid = auth.currentUser?.uid!!
        if (!party.users.contains(uid)) {
            party.users.add(uid)
            updateParty(party)
        }
    }

    override fun getCurrentUserId(): String = auth.uid!!

    override fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }
}