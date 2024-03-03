package com.example.viewandroidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.viewandroidapp.databinding.ActivityProfileSettingsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

    }

    fun onIconCloseClick(view: View) {
        // close edit
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun changeProfilePictureClick(view: View) {}
    fun onIconLogoutClick(view: View) {}
}