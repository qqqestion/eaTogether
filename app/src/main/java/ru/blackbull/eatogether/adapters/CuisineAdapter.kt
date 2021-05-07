package ru.blackbull.eatogether.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cuisine.view.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.models.CuisineType
import javax.inject.Inject

/**
 * Адаптер для отображения различных видов кухонь
 *
 */
class CuisineAdapter @Inject constructor() :
    RecyclerView.Adapter<CuisineAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CuisineType>() {
        override fun areItemsTheSame(oldItem: CuisineType , newItem: CuisineType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CuisineType , newItem: CuisineType): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this , diffCallback)

    var cuisines: List<CuisineType>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cuisine , null , false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
        val cuisine = cuisines[position]
        holder.itemView.apply {
            cbCuisine.text = cuisine.name
            cbCuisine.isChecked = cuisine.isChecked
            cbCuisine.setOnClickListener {
                cuisine.isChecked = cbCuisine.isChecked
            }
        }
    }

    override fun getItemCount(): Int = cuisines.size

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item)
}