package com.example.viewandroidapp.Moduls.Users

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.viewandroidapp.AuthModel
import com.example.viewandroidapp.MainActivity
import com.example.viewandroidapp.R

class RegisterActivity : AppCompatActivity() {
    private lateinit var authModel: AuthModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize AuthModel
        authModel = AuthModel(this)
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
                // Register user with AuthModel
                authModel.signUp(name, email, password1)
            } else {
                // Passwords don't match, display an error message
                Toast.makeText(
                    baseContext, "Passwords don't match.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Fields are empty, display an error message
            Toast.makeText(
                baseContext, "All fields are required.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
