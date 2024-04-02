package com.example.viewandroidapp

import android.content.Context
import android.content.Intent
import android.widget.ImageButton
import com.example.viewandroidapp.Moduls.Posts.CreatePostActivity
import com.example.viewandroidapp.Moduls.Users.ProfileActivity

object NavUtil {
    fun setupActivityButtons(context: Context,
                             homeButton: ImageButton,
                             searchButton: ImageButton,
                             createPostButton: ImageButton,
                             profileButton: ImageButton) {
        homeButton.setOnClickListener {
            // Check if the current activity is already HomeActivity.kt
            if (context !is HomeActivity) {
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
            }
        }

        searchButton.setOnClickListener {
            // Check if the current activity is already SearchActivity
            if (context !is SearchActivity) {
                val intent = Intent(context, SearchActivity::class.java)
                context.startActivity(intent)
            }
        }

        createPostButton.setOnClickListener {
            // Check if the current activity is already ProfileActivity
            if (context !is CreatePostActivity) {
                val intent = Intent(context, CreatePostActivity::class.java)
                context.startActivity(intent)
            }
        }

        profileButton.setOnClickListener {
            // Check if the current activity is already ProfileActivity
            if (context !is ProfileActivity) {
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}
