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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.blackbull.eatogether.utils.PlaceDataParser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.CompaniesAdapter
import ru.blackbull.eatogether.adapters.ReviewAdapter
import ru.blackbull.eatogether.db.Party
import ru.blackbull.eatogether.db.PartyManager
import ru.blackbull.eatogether.googleplacesapi.OneResult
import ru.blackbull.eatogether.googleplacesapi.PlaceDetail
import ru.blackbull.eatogether.modules.NetworkModule
import java.lang.RuntimeException


private const val KEY = "place_id"

class PlaceDetailFragment : Fragment() , ChildEventListener ,
    View.OnClickListener {
    private val theGooglePlaceApiService = NetworkModule.theGooglePlaceApiService

    private var placeId: String? = null

    private var partyList: ArrayList<Party>? = null
    private var adapter: CompaniesAdapter? = null

    private lateinit var placeNameText: TextView
    private lateinit var placeAddressText: TextView
    private lateinit var placePhoneNumberText: TextView
    private lateinit var placeIsOpenText: TextView
    private lateinit var reviewsLabel: TextView
    private lateinit var placeImage: ImageView
    private lateinit var rvReviews: RecyclerView

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
        val layout = inflater.inflate(
            R.layout.fragment_place_detail ,
            container , false
        )
        placeNameText = layout.findViewById(R.id.name)
        placeAddressText = layout.findViewById(R.id.address)
        placePhoneNumberText = layout.findViewById(R.id.phone)
        placeIsOpenText = layout.findViewById(R.id.open_now)
        placeImage = layout.findViewById(R.id.image)
        reviewsLabel = layout.findViewById(R.id.reviews_label)
        rvReviews = layout.findViewById(R.id.rv_reviews)

        FirebaseApp.initializeApp(context!!)
        val partyManager = PartyManager()
        partyList = partyManager.getByPlaceId(placeId!! , this)

        val partyRecyclerView: RecyclerView = layout.findViewById(R.id.rv_parties)

//        adapter = PartyListAdapter(context!! , partyList!!)
        adapter = CompaniesAdapter(context!! , partyList!!)
        partyRecyclerView.adapter = adapter

        layout.findViewById<Button>(R.id.create_party).setOnClickListener(this)

        val placeDetailCall: Call<OneResult> = theGooglePlaceApiService.getPlaceDetail(placeId!!)
        placeDetailCall.enqueue(object : Callback<OneResult> {
            override fun onResponse(call: Call<OneResult> , response: Response<OneResult>) {
                val responseResult = response.body()
                if (responseResult?.status ?: "" == "OK") {
                    Log.d(
                        "DebugAPI" ,
                        "Retrofit -> onResponse: success: ${responseResult?.placeDetail}"
                    )
                    updatePlaceInfo(responseResult?.placeDetail!!)
                } else {
                    Log.d(
                        "DebugAPI" ,
                        "Retrofit -> onResponse: failed: ${responseResult?.errorMessage}"
                    )
                }
            }

            override fun onFailure(call: Call<OneResult> , t: Throwable) {
                t.printStackTrace()
                Log.d("DebugAPI" , "Retrofit -> onFailure: ${t.message}")
            }
        })

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
            party.id = snapshot.key
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
                .replace(
                    R.id.layout_for_fragments ,
                    CreatePartyFragment.newInstance(
                        placeId!! ,
                        placeNameText!!.text as String , placeAddressText!!.text as String
                    )
                )
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

    private fun updatePlaceInfo(place: PlaceDetail?) {
        if (place == null) {
            throw RuntimeException("cannot get place information")
        }
        placeNameText.text = place.name
        placeAddressText.text = place.formatted_address
        placePhoneNumberText.text = place.formatted_phone_number
        placeIsOpenText.text = if (place.getIsOpen() == true) {
            "Открыто"
        } else {
            "Закрыто"
        }
        val parser = PlaceDataParser()
        if (place.photos != null) {
            Picasso.with(context)
                .load(parser.getPhotoUrl(place.photos!![0].photo_reference , 400 , 400))
                .into(placeImage)
        }

        if (place.reviews != null) {
            rvReviews.adapter = ReviewAdapter(context!! , place.reviews!!)
        } else {
            reviewsLabel.visibility = View.GONE
            rvReviews.visibility = View.GONE
        }
    }
}