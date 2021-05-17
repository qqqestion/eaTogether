package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.item_invite_user_for_lunch.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.User
import javax.inject.Inject

class InviteUserForLunchAdapter @Inject constructor() :
    RecyclerView.Adapter<InviteUserForLunchAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User , newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User , newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , differCallback)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_invite_user_for_lunch ,
                parent ,
                false
            )
        )
    }

    override fun getItemCount(): Int = users.size

    private var onUserClickListener: ((User) -> Unit)? = null

    fun setOnUserClickListener(listener: (User) -> Unit) {
        onUserClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
        val user = users[position]
        holder.itemView.apply {
            ivPhoto.load(user.mainImageUri) {
                transformations(CircleCropTransformation())
            }
            tvName.text = user.fullName()
            btnInviteUser.setOnClickListener {
                onUserClickListener?.let { click ->
                    click(user)
                }
            }
        }
    }
}