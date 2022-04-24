package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.item_user_in_party_preview.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.data.models.firebase.User
import javax.inject.Inject

/**
 * Адаптер для отображения участников компаний
 *
 */
class PartyParticipantAdapter @Inject constructor() :
    RecyclerView.Adapter<PartyParticipantAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User , newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User , newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , differCallback)

    var participants: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

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

    private var onUserClickListener: ((User) -> Unit)? = null

    fun setOnUserClickListener(listener: (User) -> Unit) {
        onUserClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
        val user = differ.currentList[position]
        holder.itemView.apply {
            ivPartyParticipantPhoto.load(user.mainImageUri) {
                transformations(CircleCropTransformation())
            }
            tvPartyParticipantName.text = "${user.firstName} ${user.lastName}"
            setOnClickListener {
                onUserClickListener?.let { click ->
                    click(user)
                }
            }
        }
    }
}