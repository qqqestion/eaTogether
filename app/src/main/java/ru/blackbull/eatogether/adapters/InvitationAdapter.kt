package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.item_invitation.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.data.models.firebase.InvitationWithUsers
import javax.inject.Inject

/**
 * Адаптер для отображения списка заявок в друзья
 *
 */
class InvitationAdapter @Inject constructor() :
    RecyclerView.Adapter<InvitationAdapter.ViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<InvitationWithUsers>() {
        override fun areItemsTheSame(
            oldItem: InvitationWithUsers ,
            newItem: InvitationWithUsers
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: InvitationWithUsers ,
            newItem: InvitationWithUsers
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , callback)

    var invitations: List<InvitationWithUsers>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_invitation ,
                parent ,
                false
            )
        )
    }

    private var onAddToFriendListClickListener: ((InvitationWithUsers) -> Unit)? = null

    fun setOnAddToFriendListClickListener(listener: ((InvitationWithUsers) -> Unit)?) {
        onAddToFriendListClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
        val user = invitations[position].inviter
        user?.let { nnUser ->
            holder.itemView.apply {
                ivPhoto.load(nnUser.mainImageUri) {
                    transformations(CircleCropTransformation())
                }
                tvName.text = nnUser.fullName()
                btnAddToFriendList.setOnClickListener {
                    onAddToFriendListClickListener?.let { click ->
                        click(invitations[position])
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return invitations.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item)
}