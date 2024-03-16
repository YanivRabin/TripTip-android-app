package com.example.viewandroidapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class CreatePostActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)


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

    fun onAddPhotoClick(view: View) {}
    fun onAddLocationClick(view: View) {}
}