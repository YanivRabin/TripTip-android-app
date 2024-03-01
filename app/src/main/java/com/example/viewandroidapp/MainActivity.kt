package com.example.viewandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onLoginButtonClick(view: View) {
        val email = findViewById<EditText>(R.id.loginEmail).text.toString()
        val password = findViewById<EditText>(R.id.loginPassword).text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Navigate to the home page
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun onNewUserTextClick(view: View) {
        // Navigate to the register page
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}