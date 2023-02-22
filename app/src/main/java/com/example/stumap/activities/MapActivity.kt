package com.example.stumap.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
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
    lateinit var btnLocate: Button
    lateinit var session: Session
    lateinit var activity: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        activity = this;
        session = Session(activity)

        btnback = findViewById(R.id.btnback);
        btnLocate = findViewById(R.id.btnLocate)
        btnback.setOnClickListener {
            onBackPressed()
        }
        btnLocate.setOnClickListener {

            val fromLat = session.getData(Constant.STARTLAT)?.toDouble() ?: 0.0
            val fromLng = session.getData(Constant.STARTLNG)?.toDouble() ?: 0.0
            val toLat = intent.getStringExtra(Constant.DESTINATIONLAT)?.toDouble()
            val toLng = intent.getStringExtra(Constant.DESTINATIONLNG)?.toDouble()
            if (toLat != null) {
                if (toLng != null) {
                    openGoogleMaps(fromLat, fromLng, toLat, toLng)
                }
            }
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
            if (destinationLat != null)
                googleMap.addMarker(destinationMarkerOptions)


            // Center the map on the start location
            if (destinationLat != null)
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            destinationLat!!,
                            destinationLng!!
                        ), 12f
                    )
                )
        }
    }

    private fun openGoogleMaps(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double) {
        val intentUri = "http://maps.google.com/maps?saddr=$fromLat,$fromLng&daddr=$toLat,$toLng"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intentUri))
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Google Maps not installed. Please download Google Maps from the Google Play Store.",
                Toast.LENGTH_LONG
            ).show()
            val marketUri = Uri.parse("market://details?id=com.google.android.apps.maps")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            startActivity(marketIntent)
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