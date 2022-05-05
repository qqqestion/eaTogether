package ru.blackbull.domain.usecases

import ru.blackbull.domain.PartyRepository
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import javax.inject.Inject

class GetPartiesByPlaceUseCase @Inject constructor(
    private val partyRepository: PartyRepository,
){

    suspend operator fun invoke(placeId: String): Either<Throwable, List<DomainPartyWithUser>> {
        return partyRepository.getPartiesByPlaceId(placeId)
    }
}