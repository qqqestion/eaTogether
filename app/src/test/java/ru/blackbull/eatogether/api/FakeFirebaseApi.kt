package ru.blackbull.eatogether.api

import android.location.Location
import com.google.firebase.firestore.GeoPoint
import ru.blackbull.eatogether.models.firebase.Match
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import java.lang.Exception

class FakeFirebaseApi : BaseFirebaseApi {

    val usersStorage = mutableListOf<User>()
    val partiesStorage = mutableListOf<Party>()
    val matchesStorage = mutableListOf<Match>()

    var authenticated: Boolean = true

    var signInAllow = true
    var signUpAllow = true

    override suspend fun updateUserLocation(location: Location) {
        val currentUser = usersStorage.find { it.id == getCurrentUserId() }
        currentUser?.let { user ->
            user.lastLocation = GeoPoint(
                location.latitude , location.longitude
            )
        }
    }

    override suspend fun searchPartyByPlace(placeId: String): List<Party> {
        return partiesStorage.filter { it.placeId == placeId }
    }

    override suspend fun addParty(party: Party) {
        partiesStorage.add(party)
    }

    override suspend fun getPartiesByCurrentUser(): List<Party> {
        return partiesStorage.filter { it.users.contains(getCurrentUserId()) }
    }

    override suspend fun getUser(uid: String): User {
        return usersStorage.find { it.id == uid } ?: throw Exception("No user with given id")
    }

    override suspend fun updateParty(party: Party) {
        partiesStorage.removeIf { party.id == it.id }
        partiesStorage.add(party)
    }

    override fun signOut() {
        authenticated = false
    }

    override suspend fun updateUser(user: User) {
        usersStorage.removeIf { user.id == it.id }
        usersStorage.add(user)
    }

    override suspend fun signIn(email: String , password: String) {
        if (signInAllow) {
            authenticated = true
        } else {
            throw Exception()
        }
    }

    override suspend fun signUpWithEmailAndPassword(user: User , password: String) {
        if (signUpAllow) {
            authenticated = true
            usersStorage.add(user)
        } else {

        }
    }

    override suspend fun getNearbyUsers(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun dislikeUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun likeUser(user: User): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getPartyById(partyId: String): Party {
        TODO("Not yet implemented")
    }

    override suspend fun getPartyParticipants(party: Party): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun addCurrentUserToParty(party: Party) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUserId(): String = "current"

    override fun isAuthenticated(): Boolean = authenticated
}