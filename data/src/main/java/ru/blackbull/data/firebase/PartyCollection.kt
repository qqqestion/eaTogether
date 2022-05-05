package ru.blackbull.data.firebase

import com.google.firebase.firestore.CollectionReference
import ru.blackbull.data.di.PARTY_COLLECTION_REF
import javax.inject.Inject
import javax.inject.Named

class PartyCollection @Inject constructor(
    @Named(PARTY_COLLECTION_REF) private val collection: CollectionReference
){
}