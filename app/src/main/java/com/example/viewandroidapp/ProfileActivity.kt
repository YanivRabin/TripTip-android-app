package com.example.viewandroidapp

import Model.Post
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viewandroidapp.Model.FireBaseModel
import com.example.viewandroidapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var fireBaseModel: FireBaseModel
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireBaseModel = FireBaseModel() // Initialize FireBaseModel
        auth = FirebaseAuth.getInstance()
        fetchAndDisplayUserData()
             fetchPosts()

        // Setup RecyclerView for posts
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = PostAdapter(fetchPosts())

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val createPostButton: ImageButton = findViewById(R.id.createPostButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, createPostButton, profileButton)
    }

    private fun fetchAndDisplayUserData() {
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
                        // Display the user's name and profile photo in the UI
                        binding.nameTextView.text = user.name
                        // Load profile photo into ImageView using Picasso or Glide
                        if (!user.profileImage.isNullOrEmpty()) {
                            Picasso.get()
                                .load(user.profileImage)
                                .into(binding.profilePicture)                        }
                    } ?: run {
                        // User not found or error occurred
                        // Handle the situation accordingly
                        // For example, display a default name or an error message
                        binding.nameTextView.text = "User Not Found"
                    }
                }
            } else { }
        } ?: run {
            // Handle case where no user is signed in
            binding.nameTextView.text = "User Not Logged In"
        }
    }

    private fun fetchPosts():List<Post> {
        // Get the currently logged-in user's email
        val currentUserEmail = auth.currentUser?.email
        // Check if the user is logged in
        currentUserEmail?.let { userEmail ->
            // Fetch posts from Firestore using the logged-in user's email
            fireBaseModel.getPosts(userEmail,
                onSuccess = { posts ->
                    // Posts fetched successfully
                    // Update the RecyclerView adapter with the fetched posts
                    val postAdapter = PostAdapter(posts)
                    recyclerView.adapter = postAdapter
                    return@getPosts
                },
                onFailure = { e ->
                    // Handle failure to fetch posts
                    // Log error or show error message
                }
            )
        }
        return emptyList<Post>()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    // Generate dummy list of posts (replace with dataBase)
//    private fun generatePosts(): ArrayList<Post> {
//        val posts = arrayListOf<Post>()
//        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//        val currentDate = LocalDateTime.now().format(formatter)
//        for (i in 1..5) {
//            posts.add(Post(
//                R.drawable.profile_picture,
//                "Yaniv is at location",
//                currentDate,
//                "This is my flight to location",
//                R.drawable.background
//            ))
//        }
//        return posts
//    }

    fun onIconSettingsClick(view: View) {
        // Open setting
        val intent = Intent(this, ProfileSettingsActivity::class.java)
        startActivity(intent)
    }
}
