package com.example.viewandroidapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CreatePostActivity : AppCompatActivity()  {

    private lateinit var nameTextView: TextView
    private lateinit var locationTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        // Initialize the TextView
        nameTextView = findViewById(R.id.name)
        locationTextView = findViewById(R.id.location)
    }

    fun onIconCloseClick(view: View) {
        finish()
    }
    fun onIconCheckClick(view: View) {
        // implement change in the database
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
//    fun onAddLocationClick(view: View) {
//        // Create an intent to start the SearchActivity
//        val intent = Intent(this, SearchActivity::class.java)
//        // Add an extra parameter to indicate the context (Search or Create)
//        intent.putExtra("context", "create")
//        startActivityForResult(intent, REQUEST_SELECT_COUNTRY)
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_SELECT_COUNTRY && resultCode == Activity.RESULT_OK) {
//            val selectedCountry = data?.getStringExtra("countryName")
//            locationTextView.text = selectedCountry
//        }
//    }
}