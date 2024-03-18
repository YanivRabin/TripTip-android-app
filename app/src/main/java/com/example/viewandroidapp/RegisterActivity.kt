package com.example.viewandroidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth
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
                // Register user with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful, navigate to the home page
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                            startActivity(intent)
                            finish()
                        } else {
                            // Registration failed, display an error message
                            Toast.makeText(
                                baseContext, "Registration failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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
