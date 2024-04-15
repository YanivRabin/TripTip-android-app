package com.example.viewandroidapp.Moduls.Posts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.Moduls.Users.ProfileActivity
import com.example.viewandroidapp.R
import com.example.viewandroidapp.databinding.ActivityCreatePostBinding
import com.example.viewandroidapp.databinding.ActivityEditPostBinding
import com.squareup.picasso.Picasso
import org.checkerframework.checker.index.qual.LengthOf

class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding
    private var model = Model.instance
    private var selectedImageUri: Uri? = null
    private var postImage: String = ""
    private var postId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent extras
        val postNameAndLocation = intent.getStringExtra("nameAndLocation")
        val postDescription = intent.getStringExtra("postDescription")
        postImage = intent.getStringExtra("postImage").toString()
        postId = intent.getStringExtra("postId").toString()

        val nameAndLocation = findViewById<TextView>(R.id.nameAndLocation)
        nameAndLocation.text = postNameAndLocation
        val description = findViewById<EditText>(R.id.postDescription)
        description.setText(postDescription)

        fetchUserPhoto(getSharedPreferences("user", MODE_PRIVATE).getString("ownerEmail", "")!!)
        Picasso.get().load(postImage).into(findViewById<ImageView>(R.id.postImage))
    }

    fun onIconCloseClick(view: View) {
        finish()
    }
    fun onIconCheckClick(view: View) {
        Log.d("PostImage", postImage)
        // Show a progress bar
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.closeButton.visibility = View.GONE
        binding.saveButton.visibility = View.GONE
        binding.addPhotoButtonLayout.visibility = View.GONE
        binding.addPhotoLayout.isEnabled = false
        binding.postDescription.isEnabled = false


        // implement change in the database
        model.editPost(postId,
                       findViewById<EditText>(R.id.postDescription).text.toString(),
                       postImage,
            onSuccess = {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                startActivity(intent)
                finish()
            },
            onFailure = {
                // Show an error message
                Toast.makeText(this, "Failed to edit post", Toast.LENGTH_SHORT).show()
                binding.loadingProgressBar.visibility = View.GONE
                binding.closeButton.visibility = View.VISIBLE
                binding.saveButton.visibility = View.VISIBLE
                binding.addPhotoButtonLayout.visibility = View.VISIBLE
                binding.addPhotoLayout.isEnabled = true
                binding.postDescription.isEnabled = true
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
            postImage = selectedImageUri.toString()
            // Load the selected image into the ImageView using Picasso
            Picasso.get().load(selectedImageUri).into(findViewById<ImageView>(R.id.postImage))
        }
    }
}