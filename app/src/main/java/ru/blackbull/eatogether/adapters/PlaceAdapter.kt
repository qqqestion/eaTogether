package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_place_preview.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.PlaceDetail


class PlaceAdapter : RecyclerView.Adapter<PlaceAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<PlaceDetail>() {
        override fun areItemsTheSame(oldItem: PlaceDetail , newItem: PlaceDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaceDetail , newItem: PlaceDetail): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , diffCallback)

    var places: List<PlaceDetail>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onItemClickListener: ((PlaceDetail) -> Unit)? = null

    fun setOnItemClickListener(listener: (PlaceDetail) -> Unit) {
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
//            val url = if (place.photos.isEmpty()) {
//                place.icon
//            } else {
//                getPhotoUrl(place.photos[0].photo_reference , 150 , 150)
//            }
//            ivPlacePreviewImage.load(url)
            tvPlacePreviewName.text = place.name
            tvPlacePreviewRating.text = place.score.toString()
            tvPlacePreviewAddress.text = place.address

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(place)
                }
            }
        }
    }

    override fun getItemCount(): Int = places.size
}