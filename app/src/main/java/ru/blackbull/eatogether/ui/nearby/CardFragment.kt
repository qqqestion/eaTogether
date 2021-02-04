package ru.blackbull.eatogether.ui.nearby

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_card.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.NearbyUserAdapter


class CardFragment : Fragment(R.layout.fragment_card) {

    private val nearbyViewModel: NearbyViewModel by viewModels()
    private lateinit var usersAdapter: NearbyUserAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        nearbyViewModel.getNearbyUsers()
    }

    private fun subscribeToObservers() {
        nearbyViewModel.nearbyUsers.observe(viewLifecycleOwner , Observer { nearbyUsers ->
            usersAdapter.users = nearbyUsers
            Snackbar.make(
                requireView() ,
                "${usersAdapter.users.size}" ,
                Snackbar.LENGTH_LONG
            ).show()
        })
        nearbyViewModel.likedUser.observe(viewLifecycleOwner , Observer { user ->
            if (user != null) {
                nearbyViewModel.likedUser.postValue(null)
                findNavController().navigate(
                    CardFragmentDirections.actionCardFragmentToMatchFragment(user)
                )
            }
        })
    }

    private fun setupRecyclerView() {
        usersAdapter = NearbyUserAdapter()
        rvNearbyUsers.adapter = usersAdapter
        val layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean = false
        }
//        val layoutManager = LinearLayoutManager(context)
        rvNearbyUsers.layoutManager = layoutManager
        rvNearbyUsers.isNestedScrollingEnabled = false
        val callback: ItemTouchHelper.Callback = object : ItemTouchHelper.SimpleCallback(
            0 ,
            ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView ,
                viewHolder: RecyclerView.ViewHolder ,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder , direction: Int) {
                val position = viewHolder.adapterPosition
                val user = usersAdapter.users[position]
                usersAdapter.users -= user

                // TODO: удалить этот кринж блок
//                val users = nearbyViewModel.nearbyUsers.value!!
//                users.removeAt(0)
//                usersAdapter.notifyItemRemoved(0)
//                nearbyViewModel.nearbyUsers.value = users

                when (direction) {
                    ItemTouchHelper.START -> {
                        Log.d("NearbyDebug" , "dislikeUser $user")
                        nearbyViewModel.dislikeUser(user)
                    }
                    ItemTouchHelper.END -> {
                        Log.d("NearbyDebug" , "likeUser $user")
                        nearbyViewModel.likeUser(user)
                        nearbyViewModel.sendLikeNotification(user)
                    }
                    else -> {
                        Log.d("NearbyDebug" , "we got a problem")
                    }
                }
            }

        }
        ItemTouchHelper(callback).apply {
            attachToRecyclerView(rvNearbyUsers)
        }
    }
}