package ru.blackbull.eatogether.ui.main.nearby

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_nearby.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.NearbyUserAdapter
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.eatogether.other.Resource
import ru.blackbull.eatogether.ui.main.snackbar
import timber.log.Timber


@AndroidEntryPoint
class NearbyFragment : Fragment(R.layout.fragment_nearby) {

    private val nearbyViewModel: NearbyViewModel by viewModels()
    private lateinit var usersAdapter: NearbyUserAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        nearbyViewModel.getNearbyUsers()
    }

    private fun subscribeToObservers() {
        nearbyViewModel.nearbyUsers.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
            } ,
            onLoading = {

            }
        ) { nearbyUsers ->
            usersAdapter.users = nearbyUsers
        })
        nearbyViewModel.likedUser.observe(viewLifecycleOwner , Observer { event ->
            val content = event.getContentIfNotHandled()
            content?.let {
                when (it) {
                    is Resource.Success -> {
                        Timber.d("content data: ${it.data}")
                        it.data?.let { user ->
                            findNavController().navigate(
                                NearbyFragmentDirections.actionCardFragmentToMatchFragment(user)
                            )
                        }
                    }
                    is Resource.Error -> {
                        content.msg?.let { msg ->
                            snackbar(msg)
                        }
                    }
                    is Resource.Loading -> {

                    }
                }
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