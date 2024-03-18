package com.example.viewandroidapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class AuthModel(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in successful, navigate to the home page
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                    context.startActivity(intent)
                } else {
                    // Sign-in failed, display an error message
                    Log.e("AuthModel", "Login failed:", task.exception)
                    Toast.makeText(
                        context,
                        "Login failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful, navigate to the home page
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Clear back stack
                    context.startActivity(intent)
                } else {
                    // Registration failed, display an error message
                    Log.e("AuthModel", "Registration failed:", task.exception)
                    Toast.makeText(
                        context,
                        "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
