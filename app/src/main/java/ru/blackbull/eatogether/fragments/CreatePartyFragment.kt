package ru.blackbull.eatogether.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.db.Party

private const val ARG_PLACE_ID = "placeId"
private const val ARG_PLACE_NAME = "placeName"
private const val ARG_PLACE_ADDRESS = "placeAddress"

class CreatePartyFragment : Fragment() , View.OnClickListener {
    private var placeId: String? = null
    private var placeName: String? = null
    private var placeAddress: String? = null

    private var partyNameText: EditText? = null
    private var partyDescriptionText: EditText? = null
    private var partyTimeText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeId = it.getString(ARG_PLACE_ID)
            placeName = it.getString(ARG_PLACE_NAME)
            placeAddress = it.getString(ARG_PLACE_ADDRESS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_create_party , container , false)
        layout.findViewById<TextView>(R.id.place_name).text = placeName
        layout.findViewById<TextView>(R.id.place_address).text = placeAddress
        layout.findViewById<Button>(R.id.confirm_creation).setOnClickListener(this)
        layout.findViewById<Button>(R.id.cancel_creation).setOnClickListener(this)

        partyNameText = layout.findViewById(R.id.create_party_title)
        partyDescriptionText = layout.findViewById(R.id.create_party_description)
        partyTimeText = layout.findViewById(R.id.create_party_time)
        return layout
    }

    companion object {
        @JvmStatic
        fun newInstance(placeId: String, placeName: String, placeAddress: String) =
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
        val party = Party(FirebaseDatabase.getInstance().reference.child(Party.DB_PREFIX).push())
        party.title = partyNameText!!.text.toString()
        party.description = partyDescriptionText!!.text.toString()
        party.time = partyTimeText!!.text.toString()
        party.userArray[FirebaseAuth.getInstance().currentUser!!.uid] = true
        party.placeId = placeId
        if (party.isValid) {
            party.save()
        }
        (context as AppCompatActivity).onBackPressed()
    }
}