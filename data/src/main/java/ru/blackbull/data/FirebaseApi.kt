package ru.blackbull.data

import android.location.Location
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import ru.blackbull.data.models.firebase.*
import ru.blackbull.domain.Constants
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Класс, работающий с Firebase
 *
 */
class FirebaseApi
@Inject constructor() {

    val auth = FirebaseAuth.getInstance()

    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")
    private val notificationsRef = Firebase.firestore.collection("notifications")
    private val invitationsRef = Firebase.firestore.collection("invitations")
    private val lunchInvitationsRef = Firebase.firestore.collection("lunchInvitations")
    private val matchesRef = Firebase.firestore.collection("matches")

    /**
     * Обновляет поле пользователя "lastLocation" в Firestore
     *
     * @param location
     */
    suspend fun updateUserLocation(location: Location) {
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
     * Ищет компании по id места, возвращает только те, которые проходят сегодня.
     *
     * @param placeId id места из Yandex MapKit
     * @return List<Party>
     */
    suspend fun searchPartyByPlace(
        placeId: String
    ): List<Party> {
        val calendar = Calendar.getInstance()
        val startYear = calendar.get(Calendar.YEAR)
        val startMonth = calendar.get(Calendar.MONTH)
        val startDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(startYear , startMonth , startDay , 0 , 0 , 0)
        val dayStart = calendar.time
        calendar.set(startYear , startMonth , startDay , 23 , 59 , 59)
        val dayEnd = calendar.time
        Timber.d("Day start: $dayStart")
        Timber.d("Day end: $dayEnd")

        return partiesRef
            .whereEqualTo("placeId" , placeId)
            .whereGreaterThanOrEqualTo("time" , Timestamp(dayStart))
            .whereLessThanOrEqualTo("time" , Timestamp(dayEnd))
            .get()
            .await()
            .toObjects(Party::class.java)
            .onEach {
                it.isCurrentUserInParty = auth.uid in it.users
            }
    }

    /**
     * Создает компанию
     *
     * @param party
     */
    suspend fun addParty(party: Party) {
        party.id = partiesRef.add(party).await().id
    }

    /**
     * Возвращает компании для текущего пользователя
     *
     * @return
     */
    suspend fun getPartiesByCurrentUser(): List<Party> {
        return partiesRef
            .whereArrayContains("users" , auth.uid!!)
            .orderBy("time")
            .get()
            .await()
            .toObjects(Party::class.java)
            .onEach {
                it.isCurrentUserInParty = auth.uid in it.users
            }
    }

    /**
     * Gets user with given id
     *
     * @param uid
     * @return
     */
    suspend fun getUser(uid: String): User {
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
    suspend fun updateParty(party: Party) {
        partiesRef.document(party.id!!).set(party).await()
    }

    /**
     * Signs out
     *
     */
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun uploadImage(localImageUri: Uri): Uri? {
        val res = FirebaseStorage.getInstance()
            .reference
            .child(UUID.randomUUID().toString())
            .putFile(localImageUri)
            .await()
        return res.metadata?.reference?.downloadUrl?.await()
    }

    suspend fun updateUser(user: User) {
        usersRef.document(user.id!!).set(user).await()
    }

    suspend fun signIn(email: String , password: String) {
        auth.signInWithEmailAndPassword(email , password)
            .await()
    }

    /**
     * Firstly we create user account with given email and password, then save all other information
     *
     * @param user where all user info stores, except for password
     * @param password
     */
    suspend fun signUpWithEmailAndPassword(
        user: User
    ) {
        val firebaseUser: FirebaseUser? = auth.currentUser
        user.mainImageUri = Constants.DEFAULT_IMAGE_URL
        user.images += user.mainImageUri!!
        user.isRegistrationComplete = true
        usersRef.document(firebaseUser!!.uid).set(user).await()
    }

    /**
     * Gets nearby user
     * Right now there is no any location and "nearby@ check, so user basically gets all non-liked and non-disliked users
     *
     * @return
     */
    companion object h {
        var callCount = 0
    }

    suspend fun getNearbyUsers(): List<User> {
        val currentUser = getUser(auth.uid!!)
        callCount += 1
        /**
         * Generates list of users to exclude, so that it won't appear in result set
         */
        // TODO: сделать
        val excludeUsers = (currentUser.likedUsers +
                currentUser.dislikedUsers +
                currentUser.friendList +
                currentUser.id).toHashSet()
        val users = if (excludeUsers.size <= 9) {
            usersRef.whereIn(
                FieldPath.documentId() ,
                excludeUsers.toList()
            ).get().await().toObjects(User::class.java)
        } else {
            usersRef.get()
                .await()
                .toObjects(User::class.java)
                .filter { !excludeUsers.contains(it.id) }
        }
        Timber.d("users: $users")
        return users
    }

    suspend fun dislikeUser(user: User) {
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
    suspend fun likeUser(user: User): User? {
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

    suspend fun getPartyById(partyId: String): Party {
        return partiesRef
            .document(partyId)
            .get()
            .await()
            .toObject(Party::class.java) ?: throw Exception("No party with given id: $partyId")
    }

    suspend fun getPartyParticipants(party: Party): List<User> {
        return usersRef
            .whereIn(FieldPath.documentId() , party.users)
            .get()
            .await()
            .toObjects(User::class.java)
    }

    fun getCurrentUserId(): String = auth.uid!!

    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    suspend fun deleteImage(uri: Uri) {
        FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).delete().await()
    }

    suspend fun makeImageMain(uri: Uri): User {
        val user = getUser(getCurrentUserId())
        user.mainImageUri = uri.toString()
        if (!user.images.contains(uri.toString())) {
            Timber.d("User image array does not contain main image")
        }
        updateUser(user)
        return user
    }

    suspend fun addInvitation(invitation: Invitation) {
        invitationsRef.add(invitation).await()
    }

    suspend fun getInvitationsByUser(userId: String): List<Invitation> {
        return invitationsRef
            .whereEqualTo("inviter" , userId)
            .get()
            .await()
            .toObjects(Invitation::class.java)
    }

    suspend fun getInvitationWithInviterAndInvitee(inviter: String , invitee: String): Invitation? {
        return invitationsRef
            .whereEqualTo("inviter" , inviter)
            .whereEqualTo("invitee" , invitee)
            .get()
            .await()
            .toObjects(Invitation::class.java)
            .firstOrNull()
    }

    suspend fun deleteInvitation(invitation: Invitation) {
        invitationsRef
            .document(invitation.id!!)
            .delete()
            .await()
    }

    suspend fun deleteInvitationToParty(partyId: String) {
        lunchInvitationsRef
            .whereEqualTo("partyId" , partyId)
            .whereEqualTo("invitee" , getCurrentUserId())
            .get()
            .await()
            .toObjects(LunchInvitation::class.java)
            .forEach { invitation ->
                lunchInvitationsRef
                    .document(invitation.id!!)
                    .delete()
                    .await()
            }
    }

    suspend fun getFriendList(user: User): List<User> {
        return usersRef
            .whereIn(
                FieldPath.documentId() ,
                user.friendList
            )
            .get()
            .await()
            .toObjects(User::class.java)
    }

    suspend fun getInvitationsForUser(invitee: String): List<Invitation> {
        return invitationsRef
            .whereEqualTo("invitee" , invitee)
            .get()
            .await()
            .toObjects(Invitation::class.java)
    }

    suspend fun addLunchInvitation(invitation: LunchInvitation) {
        lunchInvitationsRef.add(invitation).await()
    }

    suspend fun getLunchInvitationsForUser(invitee: String): List<LunchInvitation> {
        return lunchInvitationsRef
            .whereEqualTo("invitee" , invitee)
            .get()
            .await()
            .toObjects(LunchInvitation::class.java)
    }

    suspend fun getPastPartiesByUser(userId: String): List<Party> {
        val time = Calendar.getInstance().time
        return partiesRef
            .whereArrayContains("users" , userId)
            .whereLessThan("time" , Timestamp(time))
            .get()
            .await()
            .toObjects(Party::class.java)
    }
}