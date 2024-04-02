package com.example.viewandroidapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import com.example.viewandroidapp.Moduls.Posts.PostsActivity
import com.example.viewandroidapp.databinding.ActivitySearchBinding
import com.google.android.gms.location.LocationServices
import java.io.IOException

class SearchActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchBinding
    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding the countries list
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // handle the navbar to display or hide
        val navbarLayout = findViewById<LinearLayout>(R.id.navbar)
        val context = intent.getStringExtra("context")
        if (context == "create") {
            navbarLayout.visibility = View.GONE
        } else {
            navbarLayout.visibility = View.VISIBLE
        }

        // handle the list
        val countries = resources.getStringArray(R.array.countries_coordinate_array)
        val countriesAdapter : ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, countries.map { it.split(",")[0] }
        )

        // Add the "Current Location" option
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request permission again
            requestLocationPermission()
        } else {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLocationName = getCurrentLocationName(it)
                    countriesAdapter.insert("Current Location - $currentLocationName", 0)
                }
            }
        }

        // handle list action
        binding.countriesList.adapter = countriesAdapter
        binding.countriesList.setOnItemClickListener { _, _, position, _ ->
            var selectedCountry = countriesAdapter.getItem(position)
            selectedCountry = selectedCountry?.removePrefix("Current Location - ")
            if (context == "create") {
                // If the context is for creating posts, return the selected country
                val resultIntent = Intent()
                resultIntent.putExtra("countryName", selectedCountry)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                // If the context is for searching, navigate to the posts for the selected country
                val intent = Intent(this, PostsActivity::class.java)
                intent.putExtra("countryName", selectedCountry)
                startActivity(intent)
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                if (countries.contains(query)) {
                    countriesAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                countriesAdapter.filter.filter(newText)
                return false
            }
        })

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val createPostButton: ImageButton = findViewById(R.id.createPostButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, createPostButton, profileButton)
    }

    private fun getCurrentLocationName(location: Location): String {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val countryName = address.countryName
                    return countryName ?: "Unknown Location"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Unknown Location"
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }
}