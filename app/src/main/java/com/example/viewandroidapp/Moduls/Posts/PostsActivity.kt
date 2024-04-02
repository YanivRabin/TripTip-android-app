package com.example.viewandroidapp.Moduls.Posts

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.NavUtil
import com.example.viewandroidapp.R
import com.example.viewandroidapp.databinding.ActivityPostsBinding



class PostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PostViewModel
    private var model = Model.instance

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // Retrieve country name passed from HomeActivity and change the text
        val location = intent.getStringExtra("countryName")
        binding.countryNameTextView.text = location
        val posts = model.getAllPostsByLocation(location!!)


        // Setup RecyclerView for posts
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        viewModel.posts?.observe(this) {
            recyclerView.adapter = PostAdapter(it)
        }

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
}