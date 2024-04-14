package com.example.viewandroidapp.Moduls.Posts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Photo
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.Model.Post
import com.example.viewandroidapp.Moduls.Users.ProfileActivity
import com.example.viewandroidapp.R
import com.example.viewandroidapp.SearchActivity
import com.example.viewandroidapp.databinding.ActivityCreatePostBinding
import com.example.viewandroidapp.databinding.ActivityProfileSettingsBinding
import com.google.firebase.firestore.ServerTimestamp
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreatePostActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var nameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var postDescription: TextView
    private var model = Model.instance
    private var selectedImageUri: Uri? = null
    private var ownerImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("CreatePostActivity onCreate", "User: ${intent.getStringExtra("ownerEmail")}")
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the TextView
        nameTextView = findViewById(R.id.name)
        locationTextView = findViewById(R.id.location)
        postDescription = findViewById(R.id.postDescription)

        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val ownerName = sharedPreferences.getString("ownerName", "")
        nameTextView.text = ownerName
        fetchUserPhoto(getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)
    }

    fun onIconCloseClick(view: View) {
        finish()
    }
    fun onIconCheckClick(view: View) {
        Log.d("posts","email" + getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)
//        fetchUserPhoto(getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)
        // Get the text from the EditText fields
        val name = nameTextView.text.toString()
        val description = postDescription.text.toString()
        val location = locationTextView.text.toString()

        if (description.isEmpty() || location == "Add location" || selectedImageUri == null) {
            // Show an error message to the user
            Toast.makeText(this, "You must enter description, location and upload photo", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.saveButton.visibility = View.GONE
        binding.locationIcon.visibility = View.GONE
        binding.location.isEnabled = false
        binding.addPhotoText.visibility = View.GONE
        binding.addPhotoIcon.visibility = View.GONE
        binding.postDescription.isEnabled = false

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
            runOnUiThread {
                user.profileImage.let { imageUrl ->
                    // Load the profile image using Glide
                    Glide.with(binding.profilePicture.context)
                        .load(imageUrl)
                        .into(binding.profilePicture)
                    ownerImage = imageUrl
                }
            }
        }
    }

    fun onAddPhotoClick(view: View) {
        // Create an intent to open the image picker
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    @SuppressLint("SetTextI18n")
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            // Load the selected image into the ImageView using Picasso
            Picasso.get().load(selectedImageUri).into(findViewById<ImageView>(R.id.addPhoto))

            // Hide the "Add photo" text and icon
            findViewById<TextView>(R.id.addPhotoText).text = "change photo"
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