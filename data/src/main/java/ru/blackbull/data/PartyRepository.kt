package ru.blackbull.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.blackbull.data.models.firebase.toParty
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.DomainParty
import ru.blackbull.domain.models.DomainPartyWithUser
import ru.blackbull.domain.models.DomainUser
import timber.log.Timber
import javax.inject.Inject

class PartyRepository
constructor(
    private val firebaseApi: FirebaseApi
) : PartyDataSource {

    override suspend fun searchPartyByPlace(
        placeId: String
    ): Either<Throwable , List<DomainPartyWithUser>> = withContext(Dispatchers.IO) {
        safeCall {
            Either.Right(
                firebaseApi.searchPartyByPlace(placeId)
                    .map { party ->
                        val users = party.users
                            .map { firebaseApi.getUser(it).toDomainUser() }
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
                firebaseApi.addParty(party.toParty())
                Either.Right(Unit)
            }
        }

    override suspend fun updateParty(party: DomainParty) = withContext(Dispatchers.IO) {
        firebaseApi.updateParty(party.toParty())
    }

    override suspend fun getPartiesByCurrentUser(): Either<Throwable , List<DomainPartyWithUser>> =
        withContext(Dispatchers.IO) {
            safeCall {
                Either.Right(
                    firebaseApi.getPartiesByCurrentUser()
                        .map { party ->
                            val users = party.users
                                .map { firebaseApi.getUser(it).toDomainUser() }
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

    override suspend fun getPartyById(partyId: String): Either<Throwable , DomainParty> =
        withContext(Dispatchers.IO) {
            safeCall {
                val party = firebaseApi.getPartyById(partyId).toDomainParty()
                party.isCurrentUserInParty = firebaseApi.getCurrentUserId() in party.users
                Either.Right(party)
            }
        }

    override suspend fun getPartyParticipants(
        party: DomainParty
    ): Either<Throwable , List<DomainUser>> = withContext(Dispatchers.IO) {
        safeCall {
            Timber.d("Start working")
            Timber.d("Done!")

            Either.Right(firebaseApi
                .getPartyParticipants(party.toParty())
                .map { it.toDomainUser() }
            )
        }
    }

    override suspend fun addCurrentUserToParty(party: DomainParty) = withContext(Dispatchers.IO) {
        val uid = firebaseApi.getCurrentUserId()
        if (!party.users.contains(uid)) {
            party.users.add(uid)
            firebaseApi.updateParty(party.toParty())
            firebaseApi.deleteInvitationToParty(party.id!!)
        }
    }

    override suspend fun leaveParty(party: DomainParty): Either<Throwable , Unit> =
        withContext(Dispatchers.IO) {
            safeCall {
                party.users -= firebaseApi.getCurrentUserId()
                firebaseApi.updateParty(party.toParty())
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