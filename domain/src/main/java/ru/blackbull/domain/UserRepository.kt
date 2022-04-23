package ru.blackbull.domain

import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.Statistic
import ru.blackbull.domain.models.firebase.DomainInvitationWithUsers
import ru.blackbull.domain.models.firebase.DomainLunchInvitationWithUsers
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.models.firebase.FriendState
import ru.blackbull.domain.usecases.GetUserError
import ru.blackbull.domain.usecases.UpdateUserError

interface UserRepository {

    suspend fun getCurrentUser(): Either<GetUserError, DomainUser>

    suspend fun getUser(uid: String): Either<GetUserError, DomainUser>

    fun getCurrentUserId(): String

    suspend fun updateUser(user: DomainUser): Either<UpdateUserError, DomainUser>

//    suspend fun uploadImage(uri: String): Either<UploadError, Unit>

    suspend fun getNearbyUsers(): Either<Throwable, MutableList<DomainUser>>

    suspend fun dislikeUser(user: DomainUser): Either<Throwable, Unit>

    suspend fun likeUser(user: DomainUser): Either<Throwable, DomainUser?>

    suspend fun deleteImage(uri: String): Either<Throwable, DomainUser>

    suspend fun makeImageMain(uri: String): Either<Throwable, DomainUser>

    suspend fun addToFriendList(user: DomainUser): Either<Throwable, FriendState>

    suspend fun checkUserStatus(user: DomainUser): Either<Throwable, FriendState>

    suspend fun getFriendList(): Either<Throwable, List<DomainUser>>

    suspend fun getFriendListForParty(partyId: String): Either<Throwable, List<DomainUser>>

    suspend fun getInvitationList(): Either<Throwable, List<DomainInvitationWithUsers>>

    suspend fun sendLunchInvitation(partyId: String, user: DomainUser): Either<Throwable, Unit>

    suspend fun getLunchInvitations(): Either<Throwable, List<DomainLunchInvitationWithUsers>>

    suspend fun getStatistic(): Either<Throwable, Statistic>
}

sealed interface UploadError