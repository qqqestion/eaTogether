package ru.blackbull.eatogether.ui.main

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.android.synthetic.main.fragment_map.*
import ru.blackbull.eatogether.R
import timber.log.Timber

class MapActivity : AppCompatActivity() , Session.SearchListener , CameraListener {

    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    private var mapView: MapView? = null
    private var searchEdit: EditText? = null

    private fun submitQuery(query: String) {
        Timber.d("Query: $query")
        searchSession = searchManager!!.submit(
            query ,
            VisibleRegionUtils.toPolygon(mapView!!.map.visibleRegion) ,
            SearchOptions() ,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("3f863532-8f11-409b-9f01-410fec3a2c9b")
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        setContentView(R.layout.fragment_map)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        mapView = findViewById<View>(R.id.yandexMapView) as MapView
        mapView!!.map.addCameraListener(this)
        searchEdit = findViewById<View>(R.id.etMapSearchPlaces) as EditText
        searchEdit!!.setOnEditorActionListener { textView , actionId , keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(searchEdit!!.text.toString())
            }
            false
        }
        mapView!!.map.move(
            CameraPosition(Point(59.945933 , 30.320045) , 14.0f , 0.0f , 0.0f)
        )
        fab.setOnClickListener {
//            snackbar("Clicked")
            Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show()
            submitQuery("kfc")
        }
    }


    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    override fun onSearchResponse(response: Response) {
        val mapObjects = mapView!!.map.mapObjects
        mapObjects.clear()
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation ,
                    ImageProvider.fromResource(this , R.drawable.search_result)
                )
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage = "Unknown error"
        if (error is RemoteError) {
            errorMessage = "Remote error"
        } else if (error is NetworkError) {
            errorMessage = "Network error"
        }
        Toast.makeText(this , errorMessage , Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map ,
        cameraPosition: CameraPosition ,
        cameraUpdateReason: CameraUpdateReason ,
        finished: Boolean
    ) {
        if (finished) {
            submitQuery(searchEdit!!.text.toString())
        }
    }
}