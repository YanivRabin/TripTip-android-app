package com.example.viewandroidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.viewandroidapp.databinding.ActivityPostsBinding

class PostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve country name passed from HomeActivity and change the text
        val countryName = intent.getStringExtra("countryName")
        val greeting = "Welcome to $countryName"
        binding.countryNameTextView.text = greeting

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, profileButton)
    }
}