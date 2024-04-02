package com.example.viewandroidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import com.example.viewandroidapp.Moduls.Posts.PostsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class HomeActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var myMap : GoogleMap
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // google maps
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val createPostButton: ImageButton = findViewById(R.id.createPostButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, createPostButton, profileButton)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.uiSettings.isZoomControlsEnabled = true
        myMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        myMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
        // Fetch countries and their coordinates from the resource array
        val countriesArray = resources.getStringArray(R.array.countries_coordinate_array).map { it.split(",")[0] }
        val countriesCoordinateArray = resources.getStringArray(R.array.countries_coordinate_array)

        // Check if the two arrays have the same length
        if (countriesArray.size == countriesCoordinateArray.size) {
            for (i in countriesArray.indices) {
                val countryName = countriesArray[i]
                val coordinates = countriesCoordinateArray[i].split(",")
                if (coordinates.size == 3) {
                    val lat = coordinates[1].toDoubleOrNull()
                    val lng = coordinates[2].toDoubleOrNull()
                    if (lat != null && lng != null) {
                        val countryLatLng = LatLng(lat, lng)
                        val markerOptions = MarkerOptions()
                            .position(countryLatLng)
                            .title(countryName)
                        myMap.addMarker(markerOptions)
                    }
                }
            }
        }
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(this, PostsActivity::class.java)
        intent.putExtra("countryName", marker.title)
        startActivity(intent)
        return true
    }
}

data class Country(val name: String, val latLng: LatLng)