package com.example.viewandroidapp.Moduls.Posts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.Model.Post
import com.example.viewandroidapp.Moduls.Users.ProfileActivity
import com.example.viewandroidapp.R
import com.example.viewandroidapp.SearchActivity
import com.google.firebase.firestore.ServerTimestamp
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreatePostActivity : AppCompatActivity()  {

    private lateinit var nameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var postDescription: TextView
    private var model = Model.instance

    private val ownerEmail: String = ""
    private val ownerName: String = ""
    private val ownerImage: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("CreatePostActivity onCreate", "User: ${intent.getStringExtra("ownerEmail")}")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        // Initialize the TextView
        nameTextView = findViewById(R.id.name)
        locationTextView = findViewById(R.id.location)
        postDescription = findViewById(R.id.postDescription)
    }

    fun onIconCloseClick(view: View) {
        finish()
    }
    fun onIconCheckClick(view: View) {
        Log.d("create post","email" + getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)

        // Get the text from the EditText fields
        val name = nameTextView.text.toString()
        val description = postDescription.text.toString()
        val location = locationTextView.text.toString()

        // Create a new Post object
        val post = Post(
            ownerEmail = getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!,
            ownerName = getSharedPreferences("user", MODE_PRIVATE).getString("ownerName", "")!!,
            ownerImage = getSharedPreferences("user", MODE_PRIVATE).getInt("ownerImage", 0),
            description = description,
            photo = 0, // TODO Add photo
            location = location,
            insertionTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            ),
            lastUpdateTime = System.currentTimeMillis()
        )


        Log.d("create post internal", "onIconCheckClick")

        // Pass the post to the ViewModel or save it to the database
        // For example:
        Log.d("FireBaseModel save post", "Saving post: $post")
        model.savePost(post, callback = {
            Log.d("FireBaseModel save post", "Saving post: $post")
            val intent = Intent(this, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
            startActivity(intent)
            finish()
        })
    }

    fun onAddPhotoClick(view: View) {
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedCountry = data?.getStringExtra("countryName")
                locationTextView.text = selectedCountry
            }
        }

    fun onAddLocationClick(view: View) {
        // Create an intent to start the SearchActivity
        val intent = Intent(this, SearchActivity::class.java)
        // Add an extra parameter to indicate the context (Search or Create)
        intent.putExtra("context", "create")
        startForResult.launch(intent)
    }
}