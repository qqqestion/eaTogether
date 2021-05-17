package ru.blackbull.eatogether.repositories

import android.location.Location
import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.blackbull.eatogether.api.FirebaseApi
import ru.blackbull.eatogether.models.InvitationWithUser
import ru.blackbull.eatogether.models.firebase.FriendState
import ru.blackbull.eatogether.models.firebase.Invitation
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.models.firebase.User
import ru.blackbull.eatogether.other.Constants
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.other.safeCall
import timber.log.Timber
import javax.inject.Inject


class FirebaseRepository @Inject constructor(
    private val firebaseApi: FirebaseApi
) {

    val matchesRef = Firebase.firestore.collection("matches")

    suspend fun updateUserLocation(location: Location): Unit = withContext(Dispatchers.IO) {
        firebaseApi.updateUserLocation(location)
    }

    suspend fun searchPartyByPlace(
        placeId: String
    ): Resource<List<Party>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.searchPartyByPlace(placeId))
        }
    }

    suspend fun addParty(party: Party): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            firebaseApi.addParty(party)
            Resource.Success(Unit)
        }
    }

    suspend fun updateParty(party: Party) = withContext(Dispatchers.IO) {
        firebaseApi.updateParty(party)
    }

    suspend fun getPartiesByCurrentUser(): Resource<List<Party>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getPartiesByCurrentUser())
        }
    }

    suspend fun getCurrentUser(): Resource<User> = getUser(getCurrentUserId())

    suspend fun getUser(uid: String): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getUser(uid))
        }
    }

    fun getCurrentUserId(): String = firebaseApi.getCurrentUserId()

    fun signOut() {
        firebaseApi.signOut()
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            // Чтобы не фотография из firebase не загружалась повторно в firebase
            // TODO: добавить обновление фотографии
            val imageUris = mutableListOf<String>()
            Timber.d("Local uris: ${user.images}")

            for (image in user.images) {
                val imageUri = Uri.parse(image)
                if (imageUri.host != "firebasestorage.googleapis.com") {
                    val remoteImageUri = firebaseApi.uploadImage(imageUri).toString()
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
            Timber.d("Firebase uris: $imageUris")
            firebaseApi.updateUser(user)
            Resource.Success(user)
        }
    }

    fun isAuthenticated(): Boolean {
        return firebaseApi.isAuthenticated()
    }

    suspend fun signIn(email: String , password: String) = withContext(Dispatchers.IO) {
        safeCall {
            firebaseApi.signIn(email , password)
            Resource.Success(Unit)
        }
    }

    suspend fun signUpWithEmailAndPassword(
        user: User
    ) = withContext(Dispatchers.IO) {
        safeCall {
            firebaseApi.signUpWithEmailAndPassword(user)
            Resource.Success(Unit)
        }
    }

    suspend fun getNearbyUsers(): Resource<MutableList<User>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getNearbyUsers().toMutableList())
        }
    }

    suspend fun dislikeUser(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            val curUser = firebaseApi.getUser(getCurrentUserId())
            curUser.dislikedUsers += user.id!!
            firebaseApi.updateUser(curUser)
            Resource.Success(Unit)
        }
    }

    suspend fun likeUser(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.likeUser(user))
        }
    }

    suspend fun getPartyById(partyId: String): Resource<Party> = withContext(Dispatchers.IO) {
        safeCall {
            val party = firebaseApi.getPartyById(partyId)
            party.isCurrentUserInParty = getCurrentUserId() in party.users
            Resource.Success(party)
        }
    }

    suspend fun getPartyParticipants(
        party: Party
    ): Resource<List<User>> = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(firebaseApi.getPartyParticipants(party))
        }
    }

    suspend fun addCurrentUserToParty(party: Party) = withContext(Dispatchers.IO) {
        val uid = getCurrentUserId()
        if (!party.users.contains(uid)) {
            party.users.add(uid)
            firebaseApi.updateParty(party)
        }
    }

    suspend fun sendLikeNotification(user: User) {
//        val notification = Notification(userId = user.id , type = "like")
//        notificationsRef.add(notification).await()
    }

    suspend fun deleteImage(uri: Uri): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            val user = firebaseApi.getUser(firebaseApi.getCurrentUserId())
            val uriStr = uri.toString()
            user.images -= uriStr
            if (user.images.isEmpty()) {
                user.images += Constants.DEFAULT_IMAGE_URL
            }
            if (user.mainImageUri == uriStr) {
                user.mainImageUri = user.images.first()
            }
            if (uriStr != Constants.DEFAULT_IMAGE_URL) {
                firebaseApi.deleteImage(uri)
            }
            firebaseApi.updateUser(user)
            Resource.Success(user)
        }
    }

    suspend fun makeImageMain(uri: Uri): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            val user = firebaseApi.getUser(firebaseApi.getCurrentUserId())
            user.mainImageUri = uri.toString()
            if (!user.images.contains(uri.toString())) {
                Timber.d("User image array does not contain main image")
            }
            firebaseApi.updateUser(user)
            Resource.Success(user)
        }
    }

    suspend fun addToFriendList(user: User): Resource<FriendState> = withContext(Dispatchers.IO) {
        safeCall {
            val invitationFromAnotherUser =
                firebaseApi.getInvitationWithInviterAndInvitee(user.id!! , getCurrentUserId())
            if (invitationFromAnotherUser == null) {
                val invitation = Invitation(inviter = getCurrentUserId() , invitee = user.id)
                firebaseApi.addInvitation(invitation)
                Resource.Success(FriendState.INVITATION_SENT)
            } else {
                firebaseApi.deleteInvitation(invitationFromAnotherUser)
                val currentUser = firebaseApi.getUser(getCurrentUserId())
                currentUser.friendList += user.id!!
                firebaseApi.updateUser(currentUser)
                user.friendList += currentUser.id!!
                firebaseApi.updateUser(user)
                Resource.Success(FriendState.FRIEND)
            }
        }
    }

    suspend fun checkUserStatus(user: User): Resource<FriendState> =
        withContext(Dispatchers.IO) {
            safeCall {
                if (user.id == getCurrentUserId()) {
                    Resource.Success(FriendState.ITSELF)
                } else if (!user.friendList.contains(getCurrentUserId())) {
                    val invitations = firebaseApi.getInvitationsByUser(getCurrentUserId())
                    val isInvitationSent = invitations.find { it.invitee == user.id } != null

                    if (isInvitationSent) {
                        Resource.Success(FriendState.INVITATION_SENT)
                    } else {
                        Resource.Success(FriendState.UNFRIEND)
                    }
                } else {
                    Resource.Success(FriendState.FRIEND)
                }
            }
        }

    suspend fun leaveParty(party: Party): Resource<Unit> = withContext(Dispatchers.IO) {
        safeCall {
            party.users -= getCurrentUserId()
            firebaseApi.updateParty(party)
            Resource.Success(Unit)
        }
    }

    suspend fun getFriendList(): Resource<List<User>> = withContext(Dispatchers.IO) {
        safeCall {
            val currentUser = firebaseApi.getUser(getCurrentUserId())
            val friendList = firebaseApi.getFriendList(currentUser)
            Resource.Success(friendList)
        }
    }

    suspend fun getInvitationList(): Resource<List<InvitationWithUser>> =
        withContext(Dispatchers.IO) {
            safeCall {
                val currentUser = firebaseApi.getUser(getCurrentUserId())
                val invitations = firebaseApi
                    .getInvitationsForUser(getCurrentUserId())
                    .map {
                        val inviter = firebaseApi.getUser(it.inviter!!)
                        InvitationWithUser(it.id , inviter , currentUser)
                    }
                Timber.d("Invitations: $invitations")
                Resource.Success(invitations)
            }
        }
}
