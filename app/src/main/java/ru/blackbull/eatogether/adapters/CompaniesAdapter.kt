package ru.blackbull.eatogether.adapters

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.db.Party
import ru.blackbull.eatogether.db.User


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

        var idList = ArrayList<String>()
        for ((a, b) in company.userArray) {
            idList.add(a)
        }
        var ref = FirebaseStorage.getInstance()
            .reference.child(idList[0])
            .downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).into(firstPhoto)
        }.addOnFailureListener {
                Toast.makeText(context , "Фотография не загружена" , Toast.LENGTH_SHORT).show()
        }
        when(company.userArray.size){
            1 -> {
                secondPhoto.visibility = View.GONE
            }
            2 -> {
                var ref = FirebaseStorage.getInstance()
                    .reference.child(idList[1])
                    .downloadUrl.addOnSuccessListener {
                        Glide.with(context).load(it).into(secondPhoto)
                    }.addOnFailureListener {
                        Toast.makeText(context , "Фотография не загружена" , Toast.LENGTH_SHORT)
                            .show()
                        Log.d("TagForDebug" , "onBindViewHolder: " + it.stackTraceToString())
                    }
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