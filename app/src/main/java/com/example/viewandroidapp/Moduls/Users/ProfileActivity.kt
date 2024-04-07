package com.example.viewandroidapp.Moduls.Users

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viewandroidapp.Model.FireBaseModel
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.Moduls.Posts.PostAdapter
import com.example.viewandroidapp.NavUtil
import com.example.viewandroidapp.R
import com.example.viewandroidapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var fireBaseModel: FireBaseModel
    private lateinit var auth: FirebaseAuth
    private var model = Model.instance

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireBaseModel = FireBaseModel() // Initialize FireBaseModel
        auth = FirebaseAuth.getInstance()
        fetchAndDisplayUserName()

        // Setup RecyclerView for posts
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        binding.recyclerView.visibility = View.GONE
        binding.loadingProgressBar.visibility = View.VISIBLE
        getPosts(getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val createPostButton: ImageButton = findViewById(R.id.createPostButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(
            this,
            homeButton,
            searchButton,
            createPostButton,
            profileButton
        )
    }

    private fun fetchAndDisplayUserName() {
        // Get the currently logged-in user
        val currentUser = auth.currentUser

        // Check if user is signed in (not null)
        currentUser?.let { user ->
            // Get the email of the logged-in user
            val userEmail = user.email
            // Fetch user data from Firestore using the logged-in user's email
            if (userEmail != null) {
                fireBaseModel.getUser(userEmail) { userData ->
                    userData?.let { user ->
                        // User data retrieved successfully
                        // Display the user's name in the UI
                        binding.nameTextView.text = user.name
                    } ?: run {
                        // User not found or error occurred
                        // Handle the situation accordingly
                        // For example, display a default name or an error message
                        binding.nameTextView.text = "User Not Found"
                    }
                }
            } else {
                // Handle case where user's email is null
                binding.nameTextView.text = "User Email Not Found"
            }
        } ?: run {
            // Handle case where no user is signed in
            binding.nameTextView.text = "User Not Logged In"
        }
    }

    // Generate dummy list of posts (replace with dataBase)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPosts(ownerEmail: String?) {
        Log.d("posts", "first")
        lifecycleScope.launch {
            val posts = withContext(Dispatchers.IO) {
                model.getAllPostsByOwnerEmail(ownerEmail ?: "")
            }
            // Now you have the posts list here, you can pass it to your adapter
            PostAdapter(posts)
            Log.d("posts", "Profile activity Posts : $posts")
            binding.recyclerView.visibility = View.VISIBLE
            binding.loadingProgressBar.visibility = View.GONE
        }
    }



    fun onIconSettingsClick(view: View) {
        // Open setting
        val intent = Intent(this, ProfileSettingsActivity::class.java)
        startActivity(intent)
    }
}
