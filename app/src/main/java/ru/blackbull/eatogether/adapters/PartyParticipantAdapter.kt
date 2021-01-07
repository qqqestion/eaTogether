package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.item_user_in_party_preview.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.User

class PartyParticipantAdapter : RecyclerView.Adapter<PartyParticipantAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User , newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User , newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this , differCallback)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user_in_party_preview ,
                parent ,
                false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
        val user = differ.currentList[position]
        holder.itemView.apply {
            ivPartyParticipantPhoto.load(user._imageUri)
            tvPartyParticipantName.text = "${user.firstName} ${user.lastName}"
        }
    }
}