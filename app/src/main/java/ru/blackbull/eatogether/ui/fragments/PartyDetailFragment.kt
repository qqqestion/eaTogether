package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.ui.InformationActivity
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel

private const val KEY = "partyId"

class PartyDetailFragment : Fragment() {

    private lateinit var party: Party

    private lateinit var firebaseViewModel: FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseViewModel = (activity as InformationActivity).firebaseViewModel

        arguments?.let {
            party = it.getSerializable(KEY) as Party
        }
    }

    companion object {
        fun newInstance(party: Party) = PartyDetailFragment().apply {
            arguments = Bundle().apply {
                putSerializable(KEY , party)
            }
        }
    }
}