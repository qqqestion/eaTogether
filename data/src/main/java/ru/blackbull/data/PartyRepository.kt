package ru.blackbull.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.blackbull.data.models.firebase.LunchInvitation
import ru.blackbull.data.models.firebase.Party
import ru.blackbull.data.models.firebase.User
import ru.blackbull.data.models.firebase.toParty
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.firebase.DomainParty
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import ru.blackbull.domain.models.firebase.DomainUser
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class PartyRepository
//TODO вставил inject так как он вроде должен тута быть
@Inject constructor(
) : PartyDataSource {

    val auth = FirebaseAuth.getInstance()

    private val usersRef = Firebase.firestore.collection("users")
    private val partiesRef = Firebase.firestore.collection("parties")
    private val lunchInvitationsRef = Firebase.firestore.collection("lunchInvitations")



    override suspend fun searchPartyByPlace(
        placeId: String
    ): Either<Throwable , List<DomainPartyWithUser>> = withContext(Dispatchers.IO) {

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

        safeCall {
            Either.Right(
                partiesRef
                    .whereEqualTo("placeId" , placeId)
                    .whereGreaterThanOrEqualTo("time" , Timestamp(dayStart))
                    .whereLessThanOrEqualTo("time" , Timestamp(dayEnd))
                    .get()
                    .await()
                    .toObjects(Party::class.java)
                    .onEach {
                        it.isCurrentUserInParty = auth.uid in it.users
                    }.map { party ->
                        val users = party.users
                            .map { (getUser(it) as Either.Right<DomainUser>).b}
                            .toMutableList()
                        DomainPartyWithUser(
                            party.id ,
                            party.placeId ,
                            party.isCurrentUserInParty ,
                            party.time?.seconds ,
                            users
                        )
                    }
            )
        }
    }

    override suspend fun addParty(party: DomainParty): Either<Throwable , Unit> =
        withContext(Dispatchers.IO) {
            safeCall {
                party.id = partiesRef.add(party).await().id
                Either.Right(Unit)
            }
        }

    override suspend fun updateParty(party: DomainParty): Unit = withContext(Dispatchers.IO) {
        val currParty = party.toParty()
        partiesRef.document(currParty.id!!).set(currParty).await()
    }

    override suspend fun getPartiesByCurrentUser(): List<DomainParty> = partiesRef
        .whereArrayContains("users" , auth.uid!!)
        .orderBy("time")
        .get()
        .await()
        .toObjects(Party::class.java)
        .onEach {
            it.isCurrentUserInParty = auth.uid in it.users
        }.map(Party::toDomainParty)


    override suspend fun getUser(uid: String): Either<Throwable , DomainUser> =
        withContext(Dispatchers.IO) {
            safeCall {
                Either.Right(
                    usersRef
                        .document(uid)
                        .get()
                        .await()
                        .toObject(User::class.java)!!.toDomainUser()
                )
            }
        }


    override suspend fun getPartyById(partyId: String): Either<Throwable , DomainParty> =
        withContext(Dispatchers.IO) {
            safeCall { partiesRef
                    .document(partyId)
                    .get()
                    .await()
                    .toObject(Party::class.java)?.toDomainParty()?.let {
                    it.isCurrentUserInParty = getCurrentUserId() in it.users
                    Either.Right(it)
                }
                Either.Left(Exception("No party with given id: $partyId"))
            }
        }

    override suspend fun getPartyParticipants(
        party: DomainParty
    ): Either<Throwable , List<DomainUser>> = withContext(Dispatchers.IO) {
        safeCall {
            Either.Right( usersRef
                .whereIn(FieldPath.documentId() , party.toParty().users)
                .get()
                .await()
                .toObjects(User::class.java)
                .map { it.toDomainUser() }
            )
        }
    }

    private suspend fun updateParty(party: Party) {
        partiesRef.document(party.id!!).set(party).await()
    }

    private fun getCurrentUserId(): String = auth.uid!!

    override suspend fun addCurrentUserToParty(party: DomainParty) = withContext(Dispatchers.IO) {
        val uid = getCurrentUserId()
        if (!party.users.contains(uid)) {
            party.users.add(uid)
            updateParty(party.toParty())
            lunchInvitationsRef
                .whereEqualTo("partyId" , party.id!!)
                .whereEqualTo("invitee" , uid)
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
    }

    override suspend fun leaveParty(party: DomainParty): Either<Throwable , Unit> =
        withContext(Dispatchers.IO) {
            safeCall {
                party.users -= getCurrentUserId()
                updateParty(party.toParty())
                Either.Right(Unit)
            }
        }

    private inline fun <T> safeCall(action: () -> Either<Throwable , T>): Either<Throwable , T> {
        return try {
            action()
        } catch (e: Exception) {
            Timber.d(e)
            Either.Left(e)
        }
    }
}