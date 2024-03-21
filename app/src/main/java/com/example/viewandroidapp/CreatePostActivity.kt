package com.example.viewandroidapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.viewandroidapp.Model.FireBaseModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.util.UUID
import Model.Post
import android.util.Log


class CreatePostActivity : AppCompatActivity()  {

    private lateinit var nameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var fireBaseModel: FireBaseModel
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null


    companion object {
        private const val REQUEST_SELECT_COUNTRY = 100
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        // Initialize the TextView
        nameTextView = findViewById(R.id.name)
        locationTextView = findViewById(R.id.location)
        fireBaseModel = FireBaseModel()
        auth = FirebaseAuth.getInstance()

    }

    fun onIconCloseClick(view: View) {
        finish()
    }
    fun onIconCheckClick(view: View) {
        selectedImageUri?.let { uri ->
            // Generate a unique filename for the photo

            // Upload the photo to storage
            fireBaseModel.uploadPhoto(uri.toString(), "post_image", onSuccess = { photoUrl ->
                    // Photo uploaded successfully, now save the post to Firestore
                    val currentUserEmail = auth.currentUser?.email.toString()
                    val description = findViewById<EditText>(R.id.postDescription).text.toString()
                    val location = locationTextView.text.toString()

                    if (currentUserEmail != null) {
                        val post = Post(
                            ownerEmail=currentUserEmail,
                            description= description,
                            photo=photoUrl,
                            location=location)
                        fireBaseModel.savePost(post,
                            onSuccess = {
                                Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            },
                            onFailure = { e ->
                                Log.e("CreatePostActivity", "Failed to create post: ${e.message}")
                                Toast.makeText(this, "Failed to create post: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { e ->
                    Toast.makeText(this, "Failed to upload photo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } ?: run {
            Toast.makeText(this, "Please select a photo first", Toast.LENGTH_SHORT).show()
        }
    }


    fun changePostPicture(view: View) {
        // change picture on the edit but only change the picture in the database if pressed check
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

    fun onAddLocationClick(view: View) {
        // Create an intent to start the SearchActivity
        val intent = Intent(this, SearchActivity::class.java)
        // Add an extra parameter to indicate the context (Search or Create)
        intent.putExtra("context", "create")
        startActivityForResult(intent, REQUEST_SELECT_COUNTRY)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_COUNTRY && resultCode == Activity.RESULT_OK) {
            val selectedCountry = data?.getStringExtra("countryName")
            locationTextView.text = selectedCountry
        }
    }
}