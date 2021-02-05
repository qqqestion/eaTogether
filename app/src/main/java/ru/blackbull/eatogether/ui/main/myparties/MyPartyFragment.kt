package ru.blackbull.eatogether.ui.main.myparties

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_parties_rv.*
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
            partiesAdapter.parties = parties
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
//        partiesAdapter.setOnJoinCLickListener { party ->
//            viewModel.addUserToParty(party)
//            (activity as AppCompatActivity).supportFragmentManager
//                .beginTransaction()
//                .replace(
//                    R.id.flFragment ,
//                    PartyDetailFragment.newInstance(
//                        party.id!!
//                    )
//                )
//                .addToBackStack(null)
//                .commit()
//        }

        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = partiesAdapter
        }
    }
}


