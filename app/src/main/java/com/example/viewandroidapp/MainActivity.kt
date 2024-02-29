package com.example.viewandroidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.example.viewandroidapp.databinding.ActivitySearchBinding


class MainActivity : AppCompatActivity() {

    // to use activity_search write ActivitySearchBinding, for main write ActivityMainBinding etc...
    private lateinit var binding : ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val countries = resources.getStringArray(R.array.countries_array)
        val countriesAdapter : ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, countries
        )
        binding.countriesList.adapter = countriesAdapter
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
    }
}