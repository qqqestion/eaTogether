package ru.blackbull.eatogether.ui.nearby

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_nearby.*
import ru.blackbull.data.models.firebase.toUser
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.EventObserver
import ru.blackbull.domain.Resource
import ru.blackbull.eatogether.core.BaseFragment
import timber.log.Timber


@AndroidEntryPoint
class NearbyFragment : BaseFragment(R.layout.fragment_nearby) {

    private val viewModel: NearbyViewModel by viewModels()
    private lateinit var usersAdapter: NearbyUserAdapter

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        viewModel.getNearbyUsers()
    }

    private fun subscribeToObservers() {
        viewModel.nearbyUsers.observe(viewLifecycleOwner , EventObserver(
            onError = {
                snackbar(it)
                hideLoadingBar()
            } ,
            onLoading = {
                showLoadingBar()
            }
        ) { nearbyUsers ->
            hideLoadingBar()
            usersAdapter.users = nearbyUsers.map { it.toUser() }
            tvUserListIsEmpty.isVisible = nearbyUsers.isEmpty()
        })
        viewModel.likedUser.observe(viewLifecycleOwner , { event ->
            val content = event.getContentIfNotHandled()
            content?.let {
                when (it) {
                    is Resource.Success -> {
                        Timber.d("content data: ${it.data}")
                        it.data?.let { user ->
                            snackbar("У вас совпадение с пользователем ${user.firstName + ' ' + user.lastName}")
                            findNavController().navigate(
                                NearbyFragmentDirections.actionNearbyFragmentToUserInfoFragment(user.toUser())
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
        usersAdapter.setOnCardClickListener {
//            findNavController().navigate(
//                NearbyFragmentDirections.actionNearbyFragmentToUserInfoFragment(it)
//            )
        }
        rvNearbyUsers.adapter = usersAdapter
        val layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean = false
        }
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
//                val position = viewHolder.adapterPosition
                val position = viewHolder.bindingAdapterPosition
                val user = usersAdapter.users[position]
                Timber.d("Users before deleting: ${usersAdapter.users.map { it.fullName() }}")
                val size = usersAdapter.users.size
                usersAdapter.users -= user
                Timber.d("Users after deleting: ${usersAdapter.users.map { it.fullName() }}")
                tvUserListIsEmpty.isVisible = size - 1 == 0

                when (direction) {
                    ItemTouchHelper.START -> {
                        Log.d("NearbyDebug" , "dislikeUser $user")
                        viewModel.dislikeUser(user)
                    }
                    ItemTouchHelper.END -> {
                        Log.d("NearbyDebug" , "likeUser $user")
                        viewModel.likeUser(user)
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