package com.example.viewandroidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth;
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth= FirebaseAuth.getInstance()

    }
    fun onLoginButtonClick(view: View) {
        val email = findViewById<EditText>(R.id.loginEmail).text.toString()
        val password = findViewById<EditText>(R.id.loginPassword).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Attempt to sign in user with Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign-in successful, navigate to the home page
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                        startActivity(intent)
                        finish()
                    } else {
                        // Sign-in failed, display an error message
                        Log.e("LoginActivity", "Login failed:", task.exception)
                        Toast.makeText(
                            baseContext, "Login failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            // Email or password field is empty, display an error message
            Toast.makeText(
                baseContext, "Email and password are required.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

//    fun onLoginButtonClick(view: View) {
//
//        // for testing
//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)
//        finish()
//
////        val email = findViewById<EditText>(R.id.loginEmail).text.toString()
////        val password = findViewById<EditText>(R.id.loginPassword).text.toString()
////        if (email.isNotEmpty() && password.isNotEmpty()) {
////            // Navigate to the home page
////            val intent = Intent(this, HomeActivity::class.java)
////            startActivity(intent)
////            finish()
////        }
//    }

    fun onNewUserTextClick(view: View) {
        // Navigate to the register page
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}