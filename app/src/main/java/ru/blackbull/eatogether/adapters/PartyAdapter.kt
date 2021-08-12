package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.item_party_preview.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.PartyWithUser
import java.text.SimpleDateFormat
import java.util.*


/**
 * Адаптер для отображения компаний
 *
 */
class PartyAdapter : RecyclerView.Adapter<PartyAdapter.PartyViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<PartyWithUser>() {
        override fun areItemsTheSame(oldItem: PartyWithUser , newItem: PartyWithUser): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PartyWithUser , newItem: PartyWithUser): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this , differCallback)

    var parties: List<PartyWithUser>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class PartyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): PartyViewHolder {
        return PartyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_party_preview ,
                parent ,
                false
            )
        )
    }

    private fun getFormattedTime(date: Date): String {
        val formatter = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return formatter.format(date)
    }

    override fun onBindViewHolder(holder: PartyViewHolder , position: Int) {
        val party = parties[position]
        holder.itemView.apply {
            party.time?.let { time ->
                tvPartyPreviewTime.text = getFormattedTime(time.toDate())
            }
            if (party.isCurrentUserInParty) {
                btnPartyPreviewJoin.isVisible = false
            }
            // TODO: значок + должен быть виден, когда пользователь еще не в компании
            setOnClickListener {
                onItemViewClickListener?.let {
                    it(party)
                }
            }
            btnPartyPreviewJoin.setOnClickListener {
                onJoinCLickListener?.let {
                    it(party)
                }
            }

            ivPartyPreviewFirstPhoto.load(party.users[0].mainImageUri) {
                transformations(CircleCropTransformation())
            }

            /**
             * Depends on users size we manage ui.
             * users size
             * 1 -> make second photo invisible
             * 2 -> load second user photo
             * more than 2 -> replace second user photo with ellipsis
             */
            when (party.users.size) {
                1 -> {
                    ivPartyPreviewSecondPhoto.isVisible = false
                }
                2 -> {
                    ivPartyPreviewSecondPhoto.load(party.users[1].mainImageUri!!) {
                        transformations(CircleCropTransformation())
                    }
                }
                else -> {
                    ivPartyPreviewSecondPhoto.apply {
                        load(R.drawable.ic_dots) {
                            transformations(CircleCropTransformation())
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = parties.size

    private var onItemViewClickListener: ((PartyWithUser) -> Unit)? = null
    private var onJoinCLickListener: ((PartyWithUser) -> Unit)? = null

    fun setOnItemViewClickListener(listener: (PartyWithUser) -> Unit) {
        onItemViewClickListener = listener
    }

    fun setOnJoinCLickListener(listener: (PartyWithUser) -> Unit) {
        onJoinCLickListener = listener
    }
}