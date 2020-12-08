package ru.blackbull.eatogether.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ru.blackbull.eatogether.googleplacesapi.BasicLocation
import com.squareup.picasso.Picasso
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.fragments.PlaceDetailFragment
import ru.blackbull.eatogether.utils.PlaceDataParser


class PlaceListAdapter(
    private val context: Context ,
    private val data: List<BasicLocation> ,
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
        return MyViewHolder(inflater.inflate(R.layout.place_list_item , parent , false))
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
        val url: String?
        if (place.photos.isNullOrEmpty()) {
            url = place.icon
        } else {
            url = PlaceDataParser().getPhotoUrl(place.photos!![0].photo_reference , 150 , 150)
        }
        Picasso.with(context).load(url).resize(150 , 150).into(holder.photo)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}