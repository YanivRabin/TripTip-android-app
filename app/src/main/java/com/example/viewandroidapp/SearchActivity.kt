package com.example.viewandroidapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
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
        val navbarLayout = findViewById<LinearLayout>(R.id.navbar)
        val context = intent.getStringExtra("context")
        if (context == "create") {
            // hide the navbar
            navbarLayout.visibility = View.GONE
        } else {
            // display the navbar
            navbarLayout.visibility = View.VISIBLE
        }
        binding.countriesList.adapter = countriesAdapter
        binding.countriesList.setOnItemClickListener { _, _, position, _ ->
            val selectedCountry = countriesAdapter.getItem(position)
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
}