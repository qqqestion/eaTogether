package ru.blackbull.eatogether.ui.myparties

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_parties_rv.*
import ru.blackbull.data.models.firebase.toPartyWithUser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.other.EventObserver


@AndroidEntryPoint
class MyPartyFragment : Fragment(R.layout.fragment_my_parties_rv) {

    private lateinit var partiesAdapter: PartyAdapter
    private val viewModel: MyPartyViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupAdapter(rvMyParties)
        viewModel.userParties.observe(viewLifecycleOwner , EventObserver { parties ->
            partiesAdapter.parties = parties.map { it.toPartyWithUser() }
        })
        viewModel.getPartiesByCurrentUser()
    }

    private fun setupAdapter(rv: RecyclerView) {
        partiesAdapter = PartyAdapter()
        partiesAdapter.setOnItemViewClickListener { party ->
            findNavController().navigate(
                MyPartyFragmentDirections.actionMyPartyFragmentToPartyDetailFragment(party.id!!)
            )
        }

        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = partiesAdapter
        }
    }
}


