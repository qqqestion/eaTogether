package ru.blackbull.domain

import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.firebase.DomainParty
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import ru.blackbull.domain.models.firebase.DomainUser

interface PartyDataSource {
    suspend fun searchPartyByPlace(
        placeId: String
    ): Either<Throwable , List<DomainPartyWithUser>>

    suspend fun addParty(party: DomainParty): Either<Throwable , Unit>

    suspend fun updateParty(party: DomainParty)

    suspend fun getPartiesByCurrentUser(): Either<Throwable , List<DomainPartyWithUser>>

    suspend fun getPartyById(partyId: String): Either<Throwable , DomainParty>

    suspend fun getPartyParticipants(party: DomainParty): Either<Throwable , List<DomainUser>>

    suspend fun addCurrentUserToParty(party: DomainParty)

    suspend fun leaveParty(party: DomainParty): Either<Throwable , Unit>
}