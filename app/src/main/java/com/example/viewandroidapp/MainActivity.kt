package com.example.viewandroidapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.room.Room
import com.example.viewandroidapp.Moduls.Users.RegisterActivity
import com.example.viewandroidapp.dao.AppLocalDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var authModel: AuthModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AuthModel
        authModel = AuthModel(this)

        // Check if the user is already logged in
        if (authModel.isUserLoggedIn()) {
            // If the user is already logged in, go to the home page
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity so the user cannot go back to the login screen using the back button
        }
    }

    fun onLoginButtonClick(view: View) {
        val email = findViewById<EditText>(R.id.loginEmail).text.toString()
        val password = findViewById<EditText>(R.id.loginPassword).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Attempt to sign in user using AuthModel
            authModel.signIn(email, password)
        } else {
            // Email or password field is empty, display an error message
            Toast.makeText(
                baseContext, "Email and password are required.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onNewUserTextClick(view: View) {
        // Navigate to the register page
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }


}
