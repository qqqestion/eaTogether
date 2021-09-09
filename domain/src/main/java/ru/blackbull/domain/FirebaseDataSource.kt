package ru.blackbull.domain

import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.models.*
import ru.blackbull.domain.models.firebase.DomainInvitationWithUsers
import ru.blackbull.domain.models.firebase.DomainLunchInvitationWithUsers
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.models.firebase.FriendState

interface FirebaseDataSource {
    suspend fun getCurrentUser(): Either<Throwable , DomainUser>

    suspend fun getUser(uid: String): Either<Throwable , DomainUser>

    fun getCurrentUserId(): String

    fun signOut()

    suspend fun updateUser(user: DomainUser): Either<Throwable , DomainUser>

    suspend fun signIn(email: String , password: String): Either<Throwable , Unit>

    suspend fun signUpWithEmailAndPassword(
        user: DomainUser
    ): Either<Throwable , Unit>

    suspend fun getNearbyUsers(): Either<Throwable , MutableList<DomainUser>>

    suspend fun dislikeUser(user: DomainUser): Either<Throwable , Unit>

    suspend fun likeUser(user: DomainUser): Either<Throwable , DomainUser?>

    suspend fun deleteImage(uri: String): Either<Throwable , DomainUser>

    suspend fun makeImageMain(uri: String): Either<Throwable , DomainUser>

    suspend fun addToFriendList(user: DomainUser): Either<Throwable , FriendState>

    suspend fun checkUserStatus(user: DomainUser): Either<Throwable , FriendState>

    suspend fun getFriendList(): Either<Throwable , List<DomainUser>>

    suspend fun getFriendListForParty(partyId: String): Either<Throwable , List<DomainUser>>

    suspend fun getInvitationList(): Either<Throwable , List<DomainInvitationWithUsers>>

    suspend fun sendLunchInvitation(partyId: String , user: DomainUser): Either<Throwable , Unit>

    suspend fun getLunchInvitations(): Either<Throwable , List<DomainLunchInvitationWithUsers>>

    suspend fun getStatistic(): Either<Throwable , Statistic>
}