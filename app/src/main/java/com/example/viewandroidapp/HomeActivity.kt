package com.example.viewandroidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var myMap : GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // google maps
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, profileButton)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        myMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f))
    }
}