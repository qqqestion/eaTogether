package com.example.testplacesapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        val latLng = LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lng", 0.0))
        val parser = PlaceDataParser()
        val myService: ExecutorService = Executors.newFixedThreadPool(1)
        val result = myService.submit(Callable {
            return@Callable parser.execute(latLng)
        })
        val data = result.get()
        val adapter = RestaurantListAdapter(this,data)
        val list =findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        list.layoutManager =LinearLayoutManager(this)
    }
}