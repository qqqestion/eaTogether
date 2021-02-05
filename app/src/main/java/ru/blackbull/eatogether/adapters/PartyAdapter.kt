package ru.blackbull.eatogether.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.android.synthetic.main.item_party_preview.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.Party
import ru.blackbull.eatogether.other.PhotoUtility.getFormattedTime


class PartyAdapter : RecyclerView.Adapter<PartyAdapter.PartyViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Party>() {
        override fun areItemsTheSame(oldItem: Party , newItem: Party): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Party , newItem: Party): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this , differCallback)

    var parties: List<Party>
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

    override fun onBindViewHolder(holder: PartyViewHolder , position: Int) {
        val party = parties[position]
        holder.itemView.apply {
            party.time?.let { time ->
                tvPartyPreviewTime.text = getFormattedTime(time.toDate())
            }
            if (FirebaseAuth.getInstance().uid in party.users) {
                btnPartyPreviewJoin.isVisible = false
            }
            // TODO: переписать этот ужас
            FirebaseStorage.getInstance()
                .reference.child(party.users[0])
                .downloadUrl.addOnSuccessListener { uri ->
                    ivPartyPreviewFirstPhoto.load(uri) {
                        transformations(CircleCropTransformation())
                    }
                }.addOnFailureListener { e ->
                    Log.d("TagForDebug" , "error during loading first photo" , e)
                }
            when (party.users.size) {
                1 -> {
                    ivPartyPreviewSecondPhoto.visibility = View.GONE
                }
                2 -> {
                    FirebaseStorage.getInstance()
                        .reference.child(party.users[1])
                        .downloadUrl.addOnSuccessListener { uri ->
                            ivPartyPreviewSecondPhoto.load(uri) {
                                transformations(CircleCropTransformation())
                            }
                        }.addOnFailureListener { e ->
                            Log.d("TagForDebug" , "error during loading second photo" , e)
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
        }
    }

    override fun getItemCount(): Int = parties.size

    private var onItemViewClickListener: ((Party) -> Unit)? = null
    private var onJoinCLickListener: ((Party) -> Unit)? = null

    fun setOnItemViewClickListener(listener: (Party) -> Unit) {
        onItemViewClickListener = listener
    }

    fun setOnJoinCLickListener(listener: (Party) -> Unit) {
        onJoinCLickListener = listener
    }
}