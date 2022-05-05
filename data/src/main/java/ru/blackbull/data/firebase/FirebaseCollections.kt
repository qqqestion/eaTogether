package ru.blackbull.data.firebase

enum class FirebaseCollections(
    val collectionName: String,
) {
    USERS("users"),
    PARTIES("parties"),
    FRIEND_INVITATIONS("friend_invitations"),
    LAUNCH_INVITATIONS("lunch_invitations"),
    MATCHES("matches")
}