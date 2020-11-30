package ru.blackbull.eatogether.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.db.Party

class PartyListAdapter(
    context: Context,
    private val partyList: List<Party>
) : RecyclerView.Adapter<PartyListAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(party: Party) {
            itemView.findViewById<TextView>(R.id.party_title).text = party.title
            itemView.findViewById<TextView>(R.id.party_description).text = party.description
            itemView.findViewById<TextView>(R.id.party_time).text = party.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            inflater.inflate(
                R.layout.party_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(partyList[position])
    }

    override fun getItemCount(): Int = partyList.size
}