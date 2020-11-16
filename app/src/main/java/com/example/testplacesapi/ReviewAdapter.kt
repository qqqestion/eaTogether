package com.example.testplacesapi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.testplacesapi.classesForParsingPlaceDetails.Review


class ReviewAdapter(
    private val context: Context,
    private val reviewList: List<Review>,
) : RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var name: TextView = itemView.findViewById(R.id.author_name)
        private var rating: TextView = itemView.findViewById(R.id.author_rating)
        private var text: TextView = itemView.findViewById(R.id.author_text)

        fun bind(review: Review) {
            name.text = review.author_name
            rating.text = review.rating.toString()
            text.text = review.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.review_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review: Review = reviewList[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}