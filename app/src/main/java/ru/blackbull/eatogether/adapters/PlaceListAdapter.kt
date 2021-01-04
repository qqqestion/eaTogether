package ru.blackbull.eatogether.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.ui.fragments.PlaceDetailFragment
import ru.blackbull.eatogether.util.PlaceDataParser


class PlaceListAdapter(
    private val context: Context ,
    private val data: List<BasicLocation>
) : RecyclerView.Adapter<PlaceListAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var photo: ImageView = itemView.findViewById(R.id.place_image)
        private var name: TextView = itemView.findViewById(R.id.name)
        private var rating: TextView = itemView.findViewById(R.id.rating)
        private var vicinity: TextView = itemView.findViewById(R.id.vicinity)

        fun bind(basicLocation: BasicLocation) {
            name.text = basicLocation.name
            rating.text = basicLocation.rating.toString()
            vicinity.text = basicLocation.vicinity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.item_place_preview , parent , false))
    }

    override fun onBindViewHolder(holder: MyViewHolder , position: Int) {
        holder.itemView.setOnClickListener {
            val fragment = PlaceDetailFragment.newInstance(data[position].placeId)
            if (context is AppCompatActivity) {
                context.supportFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.layout_for_fragments , fragment).commit()
            }
        }
        val place: BasicLocation = data[position]
        holder.bind(place)
        val url = if (place.photos.isNullOrEmpty()) {
            place.icon
        } else {
            PlaceDataParser().getPhotoUrl(place.photos!![0].photo_reference , 150 , 150)
        }
        holder.photo.load(url)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}