package com.example.testplacesapi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.testplacesapi.classesForParsingPlaces.Basic
import com.example.testplacesapi.classesForParsingPlaces.Result
import com.squareup.picasso.Picasso


class RestaurantListAdapter(
    private val context: Context,
    private val data: Basic,
) : RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.icon)
        private var name: TextView = itemView.findViewById(R.id.name)
        private var rating: TextView = itemView.findViewById(R.id.rating)
        private var vicinity: TextView = itemView.findViewById(R.id.vicinity)

        fun bind(result: Result) {
            name.text = result.name
            rating.text = result.rating.toString()
            vicinity.text = result.vicinity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.restaurant_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            val fragment = RestaurantDetailsFragment.newInstance(data.results[position].place_id)
            if(context is AppCompatActivity){
                context.supportFragmentManager.
                beginTransaction().addToBackStack(null).
                replace(R.id.layout_for_fragments,fragment).commit()
            }
        }
        holder.bind(data.results[position])
        Picasso.with(context).load(data.results[position].icon).resize(150,150).into(holder.icon)
    }

    override fun getItemCount(): Int {
        return data.results.size
    }
}