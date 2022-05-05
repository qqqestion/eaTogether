package ru.blackbull.eatogether.ui.cuisine_choice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cuisine.view.*
import ru.blackbull.data.models.mapkit.CuisineUi
import ru.blackbull.eatogether.R
import javax.inject.Inject

/**
 * Адаптер для отображения различных видов кухонь
 *
 */
class CuisineAdapter @Inject constructor(
    private val onCheckCuisine: (CuisineUi) -> Unit
) : RecyclerView.Adapter<CuisineAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CuisineUi>() {
        override fun areItemsTheSame(oldItem: CuisineUi, newItem: CuisineUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CuisineUi, newItem: CuisineUi): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var cuisines: List<CuisineUi>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cuisine, null, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cuisine = cuisines[position]
        holder.itemView.apply {
            cbCuisine.text = cuisine.name
            cbCuisine.isChecked = cuisine.isChecked
            cbCuisine.setOnClickListener {
                onCheckCuisine(cuisine)
            }
        }
    }

    override fun getItemCount(): Int = cuisines.size

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item)
}