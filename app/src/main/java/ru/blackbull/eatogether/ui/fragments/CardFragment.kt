package ru.blackbull.eatogether.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_card.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.NearbyUserAdapter
import ru.blackbull.eatogether.ui.NearbyActivity
import ru.blackbull.eatogether.ui.viewmodels.NearbyViewModel

class CardFragment : Fragment(R.layout.fragment_card) {

    private lateinit var nearbyViewModel: NearbyViewModel
    private lateinit var usersAdapter: NearbyUserAdapter


    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        nearbyViewModel = (activity as NearbyActivity).nearbyViewModel

        setupRecyclerView()

        ibNearbyNavigation.setOnClickListener {
            (activity as NearbyActivity).drawerLayout.openDrawer(GravityCompat.START)
        }

        nearbyViewModel.nearbyUsers.observe(viewLifecycleOwner , Observer { nearbyUsers ->
            usersAdapter.differ.submitList(nearbyUsers)
        })
        nearbyViewModel.likedUser.observe(viewLifecycleOwner , Observer { user ->
            if (user != null) {
                Snackbar.make(view , "Вы лайкнули друг друга" , Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        usersAdapter = NearbyUserAdapter()
        rvNearbyUsers.adapter = usersAdapter
        val layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean = false
        }
        rvNearbyUsers.layoutManager = layoutManager
        rvNearbyUsers.isNestedScrollingEnabled = false
        val callback: ItemTouchHelper.Callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN ,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView ,
                viewHolder: RecyclerView.ViewHolder ,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder , direction: Int) {
                val position = viewHolder.adapterPosition
                val user = usersAdapter.differ.currentList[position]
                when (direction) {
                    ItemTouchHelper.START -> {
                        Log.d("NearbyDebug" , "dislikeUser $user")
                        nearbyViewModel.dislikeUser(user)
                    }
                    ItemTouchHelper.END -> {
                        Log.d("NearbyDebug" , "likeUser $user")
                        nearbyViewModel.likeUser(user)
                    }
                    else -> {
                        Log.d("NearbyDebug" , "we got a problem")
                    }
                }
                nearbyViewModel.getNearbyUsers()
            }

        }
        ItemTouchHelper(callback).apply {
            attachToRecyclerView(rvNearbyUsers)
        }
    }
}