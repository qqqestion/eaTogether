package ru.blackbull.eatogether.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.utils.PlaceDataParser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyListAdapter
import ru.blackbull.eatogether.adapters.ReviewAdapter
import ru.blackbull.eatogether.db.Party
import ru.blackbull.eatogether.db.PartyManager


private const val KEY = "place_id"

class PlaceDetailFragment : Fragment() , ChildEventListener ,
    View.OnClickListener {

    private var placeId: String? = null

    private var partyList: ArrayList<Party>? = null
    private var adapter: PartyListAdapter? = null

    private var placeNameText: TextView? = null
    private var placeAddressText: TextView? = null

    private val TAG = "TagForDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeId = it.getString(KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_place_detail ,
            container , false)
        placeNameText = layout.findViewById(R.id.name)
        placeAddressText = layout.findViewById(R.id.address)

        val parser = PlaceDataParser()
        FirebaseApp.initializeApp(context!!)
        val partyManager = PartyManager()
        Log.d(TAG , "onCreateView: ")
        partyList = partyManager.getByPlaceId(placeId!! , this)

        val partyRecyclerView: RecyclerView = layout.findViewById(R.id.party_list)

        adapter = PartyListAdapter(context!! , partyList!!)
        partyRecyclerView.adapter = adapter
//        partyList.no
        layout.findViewById<Button>(R.id.create_party).setOnClickListener(this)


        GlobalScope.launch(Dispatchers.Main) {
            val placeData = parser.getPlaceDetail(placeId!!)
            placeNameText!!.text = placeData.name
            placeAddressText!!.text = placeData.formatted_address
            layout.findViewById<TextView>(R.id.phone).text = placeData.formatted_phone_number
            if (placeData.getIsOpen() == true) {
                layout.findViewById<TextView>(R.id.open_now).text = "Открыто"
            } else {
                layout.findViewById<TextView>(R.id.open_now).text = "Закрыто"
            }
            if (placeData.photos != null) {
                Picasso.with(layout.context)
                    .load(parser.getPhotoUrl(placeData.photos!![0].photo_reference , 400 , 400))
                    .into(layout.findViewById<ImageView>(R.id.image))
            }

            val reviewRecyclerView = layout.findViewById<RecyclerView>(R.id.review_list)
            if (placeData.reviews != null) {
                reviewRecyclerView.adapter = ReviewAdapter(context!! , placeData.reviews!!)
            } else {
                layout.findViewById<TextView>(R.id.review_hint).visibility = View.GONE
                reviewRecyclerView.visibility = View.GONE
            }
        }
        return layout
    }

    companion object {
        fun newInstance(placeID: String) =
            PlaceDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY , placeID)
                }
            }
    }

    override fun onChildAdded(snapshot: DataSnapshot , previousChildName: String?) {
        val party = snapshot.getValue(Party::class.java)
        var msg = "failed -> ${snapshot.value}"
        if (party != null && party.placeId == placeId) {
            partyList!!.add(party)
            adapter!!.notifyDataSetChanged()
            msg = "successfully -> $party"
        }
        msg = "onChildAdded: $msg"
        Log.d("TagForDebug" , msg)
    }

    override fun onChildChanged(snapshot: DataSnapshot , previousChildName: String?) {
        Log.d(TAG , "onChildChanged: not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        Log.d(TAG , "onChildRemoved: not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot , previousChildName: String?) {
        Log.d(TAG , "onChildMoved: not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        Log.d(TAG , "onCancelled: not yet implemented")
    }

    override fun onClick(p0: View?) {
        if (context is AppCompatActivity) {
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_for_fragments,
                    CreatePartyFragment.newInstance(placeId!!,
                        placeNameText!!.text as String , placeAddressText!!.text as String
                    ))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG , "onDestroyView: ")
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Party.DB_PREFIX)
        ref.removeEventListener(this)
    }
}