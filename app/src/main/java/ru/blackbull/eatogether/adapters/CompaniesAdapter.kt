package ru.blackbull.eatogether.adapters

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.db.Party


class CompaniesAdapter(
    private val context: Context ,
    private val companiesList: List<Party>
) : RecyclerView.Adapter<CompaniesAdapter.CompaniesVH>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    class CompaniesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(company: Party) {
            itemView.findViewById<TextView>(R.id.time).text = company.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): CompaniesVH {
        return CompaniesVH(
            inflater.inflate(
                R.layout.company_list_item ,
                parent ,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CompaniesVH , position: Int) {
        val company = companiesList[position]
        val firstPhoto = holder.itemView.findViewById<ImageView>(R.id.firstPhoto)
        val secondPhoto = holder.itemView.findViewById<ImageView>(R.id.secondPhoto)
        Glide.with(context).load(company.userArray[0]).into(firstPhoto)
        when(company.userArray.size){
            1 -> {
                secondPhoto.visibility = View.GONE
            }
            2->{
                Glide.with(context).load(company.userArray[1]).into(secondPhoto)
            }
            else ->{
                secondPhoto.setImageResource(R.drawable.horizontal_dots_icon)
                secondPhoto.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
            }
        }

        holder.bind(company)
    }

    override fun getItemCount(): Int {
       return companiesList.size
    }
}