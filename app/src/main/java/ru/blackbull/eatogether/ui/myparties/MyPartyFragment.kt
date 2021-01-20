package ru.blackbull.eatogether.ui.myparties

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_my_parties_rv.*
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.adapters.PartyAdapter
import ru.blackbull.eatogether.ui.MapsActivity
import ru.blackbull.eatogether.ui.MyPartiesActivity
import ru.blackbull.eatogether.ui.ProfileActivity
import ru.blackbull.eatogether.ui.map.PartyDetailFragment
import ru.blackbull.eatogether.ui.viewmodels.FirebaseViewModel

class MyPartyFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerList: ListView
    private lateinit var titles: Array<String>
    private lateinit var partiesAdapter: PartyAdapter
    lateinit var viewModel: FirebaseViewModel


    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        val layout: View = inflater.inflate(R.layout.fragment_my_parties_rv , container , false)
        val rv: RecyclerView = layout.findViewById(R.id.my_parties_rv)
        setupMenu(layout)
        setupAdapter(rv)
        viewModel = (activity as MyPartiesActivity).userPartiesViewModel
        viewModel.userParties.observe(viewLifecycleOwner , Observer {
            partiesAdapter.differ.submitList(it)
        })
        viewModel.getPartiesByCurrentUser()
        return layout
    }

    private fun setupAdapter(rv: RecyclerView) {
        partiesAdapter = PartyAdapter()
        partiesAdapter.setOnItemViewClickListener { party ->
            (activity as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment ,
                    PartyDetailFragment.newInstance(
                        party.id!!
                    )
                )
                .addToBackStack(null)
                .commit()
        }
        partiesAdapter.setOnJoinCLickListener { party ->
            viewModel.addUserToParty(party)
            (activity as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment ,
                    PartyDetailFragment.newInstance(
                        party.id!!
                    )
                )
                .addToBackStack(null)
                .commit()
        }

        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = partiesAdapter
        }
    }

    private fun setupMenu(layout: View) {
        drawerLayout = layout.findViewById(R.id.my_parties_drawer_layout)
        drawerList = layout.findViewById(R.id.my_parties_drawer)
        titles = resources.getStringArray(R.array.titles)
        drawerList.apply {
            adapter = ArrayAdapter(
                context!! ,
                android.R.layout.simple_list_item_1 ,
                titles
            )
            setOnItemClickListener { parent , view , position , id ->
                when (titles[position]) {
                    "Профиль" -> {
                        val intent = Intent(context , ProfileActivity::class.java)
                        startActivity(intent)
                    }
                    "Карта" -> {
                        val intent = Intent(context , MapsActivity::class.java)
                        startActivity(intent)
                    }
                }
                drawerLayout.closeDrawer(drawerList)
            }
        }
        layout.findViewById<ImageButton>(R.id.my_parties_menu_btn).setOnClickListener {
            my_parties_drawer_layout.openDrawer(GravityCompat.START)
        }
    }
}


