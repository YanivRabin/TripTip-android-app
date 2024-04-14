package com.example.viewandroidapp.Moduls.Posts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.R
import com.example.viewandroidapp.databinding.ActivityCreatePostBinding
import com.example.viewandroidapp.databinding.ActivityEditPostBinding
import com.squareup.picasso.Picasso

class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding
    private var model = Model.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent extras
        val postNameAndLocation = intent.getStringExtra("nameAndLocation")
        val postDescription = intent.getStringExtra("postDescription")
        val postImage = intent.getStringExtra("postImage")

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
        // implement change in the database
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
            val selectedImageUri = data?.data
            // Load the selected image into the ImageView using Picasso
            Picasso.get().load(selectedImageUri).into(findViewById<ImageView>(R.id.postImage))

            // Hide the "Add photo" text and icon
            findViewById<TextView>(R.id.addPhotoText).text = "change photo"
        }
    }
}
