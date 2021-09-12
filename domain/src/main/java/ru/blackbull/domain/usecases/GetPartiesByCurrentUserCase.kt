package ru.blackbull.domain.usecases

import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.AuthDataSource
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import ru.blackbull.domain.models.firebase.DomainUser
import javax.inject.Inject

class GetPartiesByCurrentUserCase @Inject constructor(
    private val partyRepository: PartyDataSource ,
    dispatchers: AppCoroutineDispatchers
) : UseCase<UseCase.None ,  List<DomainPartyWithUser>>(dispatchers) {


    override suspend fun doWork(params: None):  List<DomainPartyWithUser> {
        return partyRepository.getPartiesByCurrentUser().map { party ->
            val users = party.users
                .map { partyRepository.getUser(it) }
                .filterIsInstance<Either.Right<DomainUser>>()
                .map { it.b }
                .toMutableList()
            DomainPartyWithUser(
                party.id ,
                party.placeId ,
                party.isCurrentUserInParty ,
                party.time ,
                users
            )
        }
    }
}

