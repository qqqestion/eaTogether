package ru.blackbull.eatogether.api

import android.location.Location
import android.net.Uri
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User

interface BaseFirebaseApi {
    /**
     * Updates user last location
     *
     * @param location
     */
    suspend fun updateUserLocation(location: Location)

    /**
     * Searching in all parties and filtering only those that have placeId field the same as given
     *
     * @param placeId place id in Google Places API
     * @return List<Party>
     */
    suspend fun searchPartyByPlace(
        placeId: String
    ): List<Party>

    /**
     * Creates party
     *
     * @param party
     */
    suspend fun addParty(party: Party)

    /**
     * Gets party in which current user is member
     *
     * @return
     */
    suspend fun getPartiesByCurrentUser(): List<Party>

    /**
     * Gets user with given id
     *
     * @param uid
     * @return
     */
    suspend fun getUser(uid: String): User

    /**
     * Updates party
     *
     * @param party
     */
    suspend fun updateParty(party: Party)

    /**
     * Signs out
     *
     */
    fun signOut()

    suspend fun updateUser(user: User , photoUri: Uri)

    suspend fun signIn(email: String , password: String)

    /**
     * Firstly we create user account with given email and password, then save all other information
     *
     * @param user where all user info stores, except for password
     * @param password
     */
    suspend fun signUpWithEmailAndPassword(
        user: User ,
        password: String
    )

    /**
     * Gets nearby user
     * Right now there is no any location and "nearby@ check, so user basically gets all non-liked and non-disliked users
     *
     * @return
     */
    suspend fun getNearbyUsers(): List<User>

    suspend fun dislikeUser(user: User)

    /**
     * If users like each other, creates match object, adds it to firestore, and returs given user
     *
     * @param user
     * @return
     */
    suspend fun likeUser(user: User): User?

    suspend fun getPartyById(partyId: String): Party

    suspend fun getPartyParticipants(party: Party): List<User>

    suspend fun addCurrentUserToParty(party: Party)
    fun getCurrentUserId(): String
    fun isAuthenticated(): Boolean
}