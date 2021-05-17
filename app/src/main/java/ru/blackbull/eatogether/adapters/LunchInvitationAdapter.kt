package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.LunchInvitationWithUser
import javax.inject.Inject

class LunchInvitationAdapter @Inject constructor() :
    RecyclerView.Adapter<LunchInvitationAdapter.ViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<LunchInvitationWithUser>() {
        override fun areItemsTheSame(
            oldItem: LunchInvitationWithUser ,
            newItem: LunchInvitationWithUser
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LunchInvitationWithUser ,
            newItem: LunchInvitationWithUser
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , callback)

    var invitations: List<LunchInvitationWithUser>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lunch_invitation , parent , false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
        val invitation = invitations[position]
    }

    override fun getItemCount(): Int {
        return invitations.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item)
}