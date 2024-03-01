package com.example.viewandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.SearchView
import com.example.viewandroidapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    // to use activity_search write ActivitySearchBinding, for main write ActivityMainBinding etc...
    private lateinit var binding : ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding the countries list
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // handle the list
        val countries = resources.getStringArray(R.array.countries_coordinate_array)
        val countriesAdapter : ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, countries.map { it.split(",")[0] }
        )
        binding.countriesList.adapter = countriesAdapter
        binding.countriesList.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = countriesAdapter.getItem(position)
            val intent = Intent(this, PostsActivity::class.java)
            intent.putExtra("countryName", selectedCountry)
            startActivity(intent)
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
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, profileButton)
    }
}