package ru.blackbull.eatogether.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_image.view.*
import ru.blackbull.eatogether.R
import javax.inject.Inject

class ImageAdapter @Inject constructor() : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri , newItem: Uri): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: Uri , newItem: Uri): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }

    private val differ = AsyncListDiffer(this , callback)

    var images: List<Uri>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private var onActionDeleteClickListener: ((Uri) -> Unit)? = null

    fun setOnActionDeleteClickListener(listener: (Uri) -> Unit) {
        onActionDeleteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image , parent , false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
        holder.itemView.ivImage.load(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item)
}