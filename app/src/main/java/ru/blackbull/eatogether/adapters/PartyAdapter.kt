package ru.blackbull.eatogether.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_party_preview.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.firebase.Party


class PartyAdapter : RecyclerView.Adapter<PartyAdapter.PartyViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Party>() {
        override fun areItemsTheSame(oldItem: Party , newItem: Party): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Party , newItem: Party): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this , differCallback)

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
        val party = differ.currentList[position]
        holder.itemView.apply {
            time.text = party.time?.toDate().toString()
            FirebaseStorage.getInstance()
                .reference.child(party.users[0])
                .downloadUrl.addOnSuccessListener { uri ->
                    firstPhoto.load(uri)
                }.addOnFailureListener { e ->
                    Log.d("TagForDebug" , "error during loading first photo" , e)
                }
            when (party.users.size) {
                1 -> {
                    secondPhoto.visibility = View.GONE
                }
                2 -> {
                    FirebaseStorage.getInstance()
                        .reference.child(party.users[1])
                        .downloadUrl.addOnSuccessListener { uri ->
                            secondPhoto.load(uri)
                        }.addOnFailureListener { e ->
                            Log.d("TagForDebug" , "error during loading second photo" , e)
                        }
                }
                else -> {
                    secondPhoto.apply {
                        setImageResource(R.drawable.ic_dots)
                        setBackgroundColor(
                            ContextCompat.getColor(
                                context ,
                                R.color.colorAccent
                            )
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}