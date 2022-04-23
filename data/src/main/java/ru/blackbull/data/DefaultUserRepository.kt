package ru.blackbull.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.blackbull.data.firebase.AuthManager
import ru.blackbull.data.firebase.FileManager
import ru.blackbull.data.firebase.UserCollection
import ru.blackbull.data.models.firebase.Invitation
import ru.blackbull.data.models.firebase.InvitationWithUsers
import ru.blackbull.data.models.firebase.LunchInvitationWithUsers
import ru.blackbull.data.models.firebase.toUser
import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.Constants
import ru.blackbull.domain.UserRepository
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.functional.mapFailure
import ru.blackbull.domain.functional.runEither
import ru.blackbull.domain.models.Statistic
import ru.blackbull.domain.models.firebase.DomainInvitationWithUsers
import ru.blackbull.domain.models.firebase.DomainLunchInvitationWithUsers
import ru.blackbull.domain.models.firebase.DomainUser
import ru.blackbull.domain.models.firebase.FriendState
import ru.blackbull.domain.usecases.*
import timber.log.Timber
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val firebaseApi: FirebaseApi,
    private val userCollection: UserCollection,
    private val authManager: AuthManager,
    private val fileManager: FileManager,
    private val dispatchers: AppCoroutineDispatchers
) : UserRepository {

    override suspend fun getCurrentUser(): Either<GetUserError, DomainUser> =
        getUser(checkNotNull(authManager.uid))

    override suspend fun getUser(uid: String): Either<GetUserError, DomainUser> =
        withContext(dispatchers.io) {
            runEither {
                userCollection.getById(uid)
                    ?: return@withContext Either.Left(UserNotFound)
            }.mapFailure { exception ->
                when (exception) {
                    is FirebaseFirestoreException -> NoInternetError
                    else -> UnexpectedNetworkCommunicationError
                }
            }
        }

    override fun getCurrentUserId(): String = checkNotNull(authManager.uid)

//    override suspend fun uploadImage(uri: String): Either<UploadError, String> =
//        withContext(dispatchers.io) {
//            runEither { fileManager.upload(Uri.parse(uri)) ?: return@withContext  }
//        }

    override suspend fun updateUser(user: DomainUser): Either<UpdateUserError, DomainUser> =
        withContext(dispatchers.io) {
            runEither {
                val imageUris = mutableListOf<String>()

                for (image in user.images) {
                    val imageUri = Uri.parse(image)
                    if (imageUri.host != "firebasestorage.googleapis.com") {
                        val remoteImageUri = fileManager.upload(imageUri).toString()
                        if (image == user.mainImageUri) {
                            user.mainImageUri = remoteImageUri
                        }
                        imageUris.add(remoteImageUri)
                    } else {
                        imageUris.add(image)
                    }
                }
                if (Constants.DEFAULT_IMAGE_URL in imageUris && imageUris.size > 1) {
                    imageUris -= Constants.DEFAULT_IMAGE_URL
                    if (user.mainImageUri == Constants.DEFAULT_IMAGE_URL) {
                        user.mainImageUri = imageUris.first()
                    }
                }
                user.images = imageUris
                firebaseApi.updateUser(user.toUser())
                user
            }.mapFailure { exception ->
                when (exception) {
                    is FirebaseFirestoreException -> NoInternetError
                    else -> UnexpectedNetworkCommunicationError
                }
            }
        }

    override suspend fun getNearbyUsers(): Either<Throwable, MutableList<DomainUser>> =
        withContext(Dispatchers.IO) {
            safeCall {
                Either.Right(firebaseApi.getNearbyUsers()
                    .map { it.toDomainUser() }
                    .toMutableList()
                )
            }
        }

    override suspend fun dislikeUser(user: DomainUser) = withContext(Dispatchers.IO) {
        safeCall {
            val curUser = firebaseApi.getUser(getCurrentUserId())
            curUser.dislikedUsers += user.id!!
            firebaseApi.updateUser(curUser)
            Either.Right(Unit)
        }
    }

    override suspend fun likeUser(user: DomainUser): Either<Throwable, DomainUser?> =
        withContext(Dispatchers.IO) {
            safeCall {
                Either.Right(firebaseApi.likeUser(user.toUser())?.toDomainUser())
            }
        }

    override suspend fun deleteImage(uri: String): Either<Throwable, DomainUser> =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = firebaseApi.getUser(firebaseApi.getCurrentUserId())
                val androidUri = Uri.parse(uri)
                user.images -= uri
                if (user.images.isEmpty()) {
                    user.images += Constants.DEFAULT_IMAGE_URL
                }
                if (user.mainImageUri == uri) {
                    user.mainImageUri = user.images.first()
                }
                if (uri != Constants.DEFAULT_IMAGE_URL) {
                    firebaseApi.deleteImage(androidUri)
                }
                firebaseApi.updateUser(user)
                Either.Right(user.toDomainUser())
            }
        }

    override suspend fun makeImageMain(uri: String): Either<Throwable, DomainUser> =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = firebaseApi.getUser(firebaseApi.getCurrentUserId())
                user.mainImageUri = uri
                if (!user.images.contains(uri)) {
                    Timber.d("User image array does not contain main image")
                }
                firebaseApi.updateUser(user)
                Either.Right(user.toDomainUser())
            }
        }

    override suspend fun addToFriendList(user: DomainUser): Either<Throwable, FriendState> =
        withContext(Dispatchers.IO) {
            safeCall {
                val invitationFromAnotherUser =
                    firebaseApi.getInvitationWithInviterAndInvitee(user.id!!, getCurrentUserId())
                if (invitationFromAnotherUser == null) {
                    val invitation = Invitation(inviter = getCurrentUserId(), invitee = user.id)
                    firebaseApi.addInvitation(invitation)
                    Either.Right(FriendState.INVITATION_SENT)
                } else {
                    firebaseApi.deleteInvitation(invitationFromAnotherUser)
                    val currentUser = firebaseApi.getUser(getCurrentUserId())
                    currentUser.friendList += user.id!!
                    firebaseApi.updateUser(currentUser)
                    user.friendList += currentUser.id!!
                    firebaseApi.updateUser(user.toUser())
                    Either.Right(FriendState.FRIEND)
                }
            }
        }

    override suspend fun checkUserStatus(user: DomainUser): Either<Throwable, FriendState> =
        withContext(Dispatchers.IO) {
            safeCall {
                if (user.id == getCurrentUserId()) {
                    Either.Right(FriendState.ITSELF)
                } else if (!user.friendList.contains(getCurrentUserId())) {
                    val invitations = firebaseApi.getInvitationsByUser(getCurrentUserId())
                    val isInvitationSent = invitations.find { it.invitee == user.id } != null

                    if (isInvitationSent) {
                        Either.Right(FriendState.INVITATION_SENT)
                    } else {
                        Either.Right(FriendState.UNFRIEND)
                    }
                } else {
                    Either.Right(FriendState.FRIEND)
                }
            }
        }

    override suspend fun getFriendList(): Either<Throwable, List<DomainUser>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val currentUser = firebaseApi.getUser(getCurrentUserId())
                val friendList =
                    if (currentUser.friendList.isNotEmpty()) firebaseApi.getFriendList(currentUser)
                    else listOf()
                Either.Right(friendList.map { it.toDomainUser() })
            }
        }

    override suspend fun getFriendListForParty(partyId: String): Either<Throwable, List<DomainUser>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val party = firebaseApi.getPartyById(partyId)
                val currentUser = firebaseApi.getUser(getCurrentUserId())
                val friendList = firebaseApi.getFriendList(currentUser).filter {
                    !party.users.contains(it.id)
                }.filter { user ->
                    val invitations = firebaseApi.getLunchInvitationsForUser(user.id!!)
                        .filter { lunchInvitation -> lunchInvitation.partyId == partyId && lunchInvitation.inviter == getCurrentUserId() }
                    invitations.isEmpty()
                }
                Either.Right(friendList.map { it.toDomainUser() })
            }
        }

    override suspend fun getInvitationList(): Either<Throwable, List<DomainInvitationWithUsers>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val currentUser = firebaseApi.getUser(getCurrentUserId())
                val invitations = firebaseApi
                    .getInvitationsForUser(getCurrentUserId())
                    .map {
                        val inviter = firebaseApi.getUser(it.inviter!!)
                        InvitationWithUsers(it.id, inviter, currentUser)
                    }
                Timber.d("Invitations: $invitations")
                Either.Right(invitations.map { it.toDomainInvitationWithUsers() })
            }
        }

    override suspend fun sendLunchInvitation(
        partyId: String,
        user: DomainUser
    ): Either<Throwable, Unit> =
        withContext(Dispatchers.IO) {
            safeCall {
                val invitation = ru.blackbull.data.models.firebase.LunchInvitation(
                    inviter = getCurrentUserId(),
                    invitee = user.id,
                    partyId = partyId
                )
                firebaseApi.addLunchInvitation(invitation)
                Either.Right(Unit)
            }
        }

    override suspend fun getLunchInvitations(): Either<Throwable, List<DomainLunchInvitationWithUsers>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val currentUser = firebaseApi.getUser(getCurrentUserId())
                val lunchInvitations = firebaseApi
                    .getLunchInvitationsForUser(getCurrentUserId())
                    .map {
                        val inviter = firebaseApi.getUser(it.inviter!!)
                        LunchInvitationWithUsers(
                            it.id,
                            inviter,
                            currentUser,
                            it.partyId
                        )
                    }
                Timber.d("Lunch invitations: $lunchInvitations")
                Either.Right(lunchInvitations.map { it.toDomainLunchInvitationWithUsers() })
            }
        }

    override suspend fun getStatistic(): Either<Throwable, Statistic> =
        withContext(Dispatchers.IO) {
            safeCall {
                val parties = firebaseApi.getPastPartiesByUser(getCurrentUserId())
                val statistic = Statistic(
                    parties.map { it.placeId }.toHashSet().size,
                    parties.size
                )
                Either.Right(statistic)
            }
        }

    private inline fun <T> safeCall(action: () -> Either<Throwable, T>): Either<Throwable, T> {
        return try {
            action()
        } catch (e: Exception) {
            Timber.d(e)
            Either.Left(e)
        }
    }
}
