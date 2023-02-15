package com.example.stumap.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.example.stumap.R
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    lateinit var btnback: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener{
            onBackPressed()
        }


        val session = Session(this)        // Find the MapView and initialize it
        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            // Set the latitude and longitude of the start location and destination

            val startLat = session.getData(Constant.STARTLAT)?.toDouble() ?: 0.0
            val startLng = session.getData(Constant.STARTLNG)?.toDouble() ?: 0.0
            val destinationLat = intent.getStringExtra(Constant.DESTINATIONLAT)?.toDouble()
            val destinationLng = intent.getStringExtra(Constant.DESTINATIONLNG)?.toDouble()


            // Create MarkerOptions objects for the start location and destination
            val startMarkerOptions = MarkerOptions()
                .position(LatLng(startLat, startLng))
                .title("Start Location")
            val destinationMarkerOptions = MarkerOptions()
                .position(LatLng(destinationLat ?: 0.0, destinationLng ?: 0.0))
                .title("Destination")

            // Add the markers to the map
            googleMap.addMarker(startMarkerOptions)
            googleMap.addMarker(destinationMarkerOptions)

            // Center the map on the start location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(startLat, startLng), 12f))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}