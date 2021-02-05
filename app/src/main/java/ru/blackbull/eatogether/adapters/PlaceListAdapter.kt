package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.item_place_preview.view.*
import ru.blackbull.eatogether.models.googleplaces.BasicLocation
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.PhotoUtility.getPhotoUrl


class PlaceListAdapter : RecyclerView.Adapter<PlaceListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<BasicLocation>() {
        override fun areItemsTheSame(oldItem: BasicLocation , newItem: BasicLocation): Boolean {
            return oldItem.placeId == newItem.placeId
        }

        override fun areContentsTheSame(oldItem: BasicLocation , newItem: BasicLocation): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , diffCallback)

    var places: List<BasicLocation>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onItemClickListener: ((BasicLocation) -> Unit)? = null

    fun setOnItemClickListener(listener: (BasicLocation) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_place_preview ,
                parent ,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder , position: Int) {
        val place = places[position]

        holder.itemView.apply {
            val url = if (place.photos.isEmpty()) {
                place.icon
            } else {
                getPhotoUrl(place.photos[0].photo_reference , 150 , 150)
            }
            ivPlacePreviewImage.load(url)
            tvPlacePreviewName.text = place.name
            tvPlacePreviewRating.text = place.rating.toString()
            tvPlacePreviewVicinity.text = place.vicinity

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(place)
                }
            }
        }
    }

    override fun getItemCount(): Int = places.size
}