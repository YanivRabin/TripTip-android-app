package com.example.viewandroidapp.Moduls.Posts

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Photo
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.Model.Post
import com.example.viewandroidapp.Moduls.Users.ProfileActivity
import com.example.viewandroidapp.R
import com.example.viewandroidapp.SearchActivity
import com.google.firebase.firestore.ServerTimestamp
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreatePostActivity : AppCompatActivity()  {

    private lateinit var nameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var postDescription: TextView
    private var model = Model.instance
    private var selectedImageUri: Uri? = null

    private val ownerEmail: String = ""
    private val ownerName: String = ""
    private var ownerImage: String = ""


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
        Log.d("posts","email" + getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)
        fetchUserPhoto(getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)
        // Get the text from the EditText fields
        val name = nameTextView.text.toString()
        val description = postDescription.text.toString()
        val location = locationTextView.text.toString()

        // Upload the photo asynchronously
        model.uploadPhoto(selectedImageUri.toString(), "post_image",
            onSuccess = { photoUrl ->
                // Create a new Post object
                val post = Post(
                    ownerEmail = getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!,
                    ownerName = getSharedPreferences("user", MODE_PRIVATE).getString("ownerName", "")!!,
                    description = description,
                    ownerImage = ownerImage,
                    location = location,
                    photo = photoUrl,
                    insertionTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    lastUpdateTime = System.currentTimeMillis()
                )

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
            },
            onFailure = { exception ->
                // Handle failure to upload photo
                Log.e("uploadPhoto", "Failed to upload photo", exception)
                // You might want to inform the user about the failure
            }
        )
    }
    private fun fetchUserPhoto(ownerEmail: String) {
        model.getUserByEmail(ownerEmail) { user ->
            // Assuming user.profileImage is a URL
            user.profileImage.let { imageUrl ->
                ownerImage = imageUrl
            }
        }
    }

    fun onAddPhotoClick(view: View) {
        // Create an intent to open the image picker
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            // Load the selected image into the ImageView using Picasso
            Picasso.get().load(selectedImageUri).into(findViewById<ImageView>(R.id.addPhoto))

            // Hide the "Add photo" text and icon
//            findViewById<View>(R.id.addPhotoIcon).visibility = View.GONE
//            findViewById<TextView>(R.id.addPhotoIcon).visibility = View.GONE
        }
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

    fun changePostPicture(view: View) {
        // change picture on the edit but only change the picture in the database if pressed check
    }
}