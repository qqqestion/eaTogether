package ru.blackbull.eatogether.ui.myparties

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_my_parties_rv.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.ui.MapsActivity
import ru.blackbull.eatogether.ui.MyPartiesActivity
import ru.blackbull.eatogether.ui.ProfileActivity
import ru.blackbull.eatogether.ui.map.PartyDetailFragment
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel

class MyPartyFragment : Fragment(R.layout.fragment_my_parties_rv) {

    private lateinit var partiesAdapter: PartyAdapter
    private val viewModel: FirebaseViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupAdapter(rvMyParties)
        viewModel.userParties.observe(viewLifecycleOwner , Observer { parties ->
            partiesAdapter.parties = parties
        })
        viewModel.getPartiesByCurrentUser()
    }

    private fun setupAdapter(rv: RecyclerView) {
        partiesAdapter = PartyAdapter()
        partiesAdapter.setOnItemViewClickListener { party ->
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
        }
        partiesAdapter.setOnJoinCLickListener { party ->
            viewModel.addUserToParty(party)
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
        }

        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = partiesAdapter
        }
    }
}


