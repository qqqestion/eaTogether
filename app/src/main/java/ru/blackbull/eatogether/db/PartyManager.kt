package ru.blackbull.eatogether.db

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import ru.blackbull.eatogether.adapters.PartyListAdapter

// ChIJQxGOz7UtlkYRiOaXzBS81o8

class PartyManager {
    fun getByPlaceId(placeId: String , childEventListener: ChildEventListener): ArrayList<Party> {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Party.DB_PREFIX)
        val partyList = ArrayList<Party>()
        ref.addChildEventListener(childEventListener)
        return partyList
    }
}