package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_create_party.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.extensions.shortToast
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.ui.InformationActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PLACE_ID = "placeId"
private const val ARG_PLACE_NAME = "placeName"
private const val ARG_PLACE_ADDRESS = "placeAddress"

class CreatePartyFragment : Fragment(R.layout.fragment_create_party) , View.OnClickListener {
    private val TAG = "TagForDebug"

    private lateinit var firebaseViewModel: FirebaseViewModel

    private var placeId: String? = null
    private var placeName: String? = null
    private var placeAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeId = it.getString(ARG_PLACE_ID)
            placeName = it.getString(ARG_PLACE_NAME)
            placeAddress = it.getString(ARG_PLACE_ADDRESS)
        }
        firebaseViewModel = (activity as InformationActivity).firebaseViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_create_party , container , false)
        return layout
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        place_name.text = placeName
        place_address.text = placeAddress
        confirm_creation.setOnClickListener(this)
        cancel_creation.setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(placeId: String , placeName: String , placeAddress: String) =
            CreatePartyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PLACE_ID , placeId)
                    putString(ARG_PLACE_NAME , placeName)
                    putString(ARG_PLACE_ADDRESS , placeAddress)
                }
            }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.confirm_creation -> createParty()
            R.id.cancel_creation -> (context!! as AppCompatActivity).onBackPressed()
        }
    }

    private fun createParty() {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm" , Locale.US)
        val date: Date?
        try {
            date = format.parse("${pick_date.text} ${pick_time.text}")
        } catch (e: ParseException) {
            shortToast("Дата введена неправильно")
            return
        }
        val party = Party(
            title = create_party_title.text.toString() ,
            description = create_party_description!!.text.toString() ,
            time = Timestamp(date) ,
            placeId = placeId ,
            users = mutableListOf(FirebaseAuth.getInstance().currentUser!!.uid)
        )
        firebaseViewModel.addParty(party)
        (context as AppCompatActivity).onBackPressed()
    }
}