package com.example.viewandroidapp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        // Retrieve data from Intent extras
        val postProfilePicture = intent.getIntExtra("profilePicture", 0)
        val postNameAndLocation = intent.getStringExtra("nameAndLocation")
        val postDescription = intent.getStringExtra("postDescription")
        val postImage = intent.getIntExtra("postImage", 0)

        // Populate post details
        val profilePicture = findViewById<ImageView>(R.id.profilePicture)
        profilePicture.setImageResource(postProfilePicture)
        val nameAndLocation = findViewById<TextView>(R.id.nameAndLocation)
        nameAndLocation.text = postNameAndLocation
        val description = findViewById<EditText>(R.id.postDescription)
        description.setText(postDescription)
        val image = findViewById<ImageView>(R.id.postImage)
        image.setImageResource(postImage)
    }

    fun onIconCloseClick(view: View) {
        finish()
    }
    fun onIconCheckClick(view: View) {
        // implement change in the database
    }

    fun changePostPicture(view: View) {
        // change picture on the edit but only change the picture in the database if pressed check
    }
}
