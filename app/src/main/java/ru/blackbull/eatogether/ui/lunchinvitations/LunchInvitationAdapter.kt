package ru.blackbull.eatogether.ui.lunchinvitations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.item_lunch_invitation.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.data.models.firebase.LunchInvitationWithUsers
import javax.inject.Inject

/**
 * Адаптер для отображения списка приглашений в компании
 *
 */
class LunchInvitationAdapter @Inject constructor() :
    RecyclerView.Adapter<LunchInvitationAdapter.ViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<LunchInvitationWithUsers>() {
        override fun areItemsTheSame(
            oldItem: LunchInvitationWithUsers ,
            newItem: LunchInvitationWithUsers
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LunchInvitationWithUsers ,
            newItem: LunchInvitationWithUsers
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , callback)

    var invitations: List<LunchInvitationWithUsers>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lunch_invitation , parent , false)
        )
    }

    private var onViewPartyClickListener: ((String) -> Unit)? = null

    fun setOnViewPartyClickListener(listener: (String) -> Unit) {
        onViewPartyClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invitation = invitations[position]

        holder.itemView.apply {
            ivUserImage.load(invitation.inviter?.mainImageUri) {
                transformations(CircleCropTransformation())
            }
            tvUserName.text = invitation.inviter?.fullName()
            btnPartyJoin.setOnClickListener {
                onViewPartyClickListener?.let { click ->
                    click(invitation.partyId!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return invitations.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item)
}