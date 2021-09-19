package ru.blackbull.eatogether.ui.main.myparties

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_parties_rv.*
import ru.blackbull.data.models.firebase.toPartyWithUser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.other.UiStateWithData
import timber.log.Timber


@AndroidEntryPoint
class MyPartyFragment : Fragment(R.layout.fragment_my_parties_rv) {

    private lateinit var partiesAdapter: PartyAdapter
    private val viewModel: MyPartyViewModel by viewModels()

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupAdapter(rvMyParties)
        viewModel.userParties.observe(viewLifecycleOwner , { result ->
            when (result) {
                is UiStateWithData.Success -> {
                    partiesAdapter.parties = result.data.map { it.toPartyWithUser() }
                }
                is UiStateWithData.Failure -> {
                    Snackbar.make(
                        requireView() ,
                        result.messageId , Snackbar.LENGTH_SHORT
                    ).show()
                }
                is UiStateWithData.Loading -> {
                    Timber.d("Лох, пидр, нет друзей")
                }
            }
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


