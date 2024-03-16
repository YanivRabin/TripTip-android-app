package com.example.viewandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun onIconCloseClick(view: View) {
        // Navigate back to the main page
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onRegisterButtonClick(view: View) {
        val name = findViewById<EditText>(R.id.registerName).text.toString()
        val email = findViewById<EditText>(R.id.registerEmail).text.toString()
        val password1 = findViewById<EditText>(R.id.registerPassword1).text.toString()
        val password2 = findViewById<EditText>(R.id.registerPassword2).text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && password1.isNotEmpty() && password2.isNotEmpty()) {
            if (password1 == password2) {
                // Navigate to the home page
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                startActivity(intent)
                finish()
            }
        }
    }
}