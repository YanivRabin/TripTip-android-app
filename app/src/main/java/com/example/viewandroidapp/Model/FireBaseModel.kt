package com.example.viewandroidapp.Model

import Model.User
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FireBaseModel {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users") // Reference to the "users" collection in Firestore
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("profile_images")
    fun saveUser(user: User) {
        // Convert User object into a map
        val userData = hashMapOf(
            "email" to user.email,
            "password" to user.password,
            "name" to user.name,
            "profileImage" to user.profileImage,
            "token" to user.token,
            "insertionTime" to user.insertionTime
        )

        // Add or update the document in the "users" collection with the email as document ID
        usersCollection.document(user.email).set(userData)
            .addOnSuccessListener {
                println("User successfully saved to Firestore")
            }
            .addOnFailureListener { e ->
                println("Error saving user to Firestore: $e")
            }
    }

    fun getUser(email: String, callback: (User?) -> Unit) {
        usersCollection.document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                // Log the error
                Log.e("FireBaseModel", "Error fetching user data", e)
                callback(null)
            }
    }

    fun updateProfilePicture(email: String, newProfilePictureUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(email).update("profileImage", newProfilePictureUrl)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun uploadPhoto(photoUri: Uri, userEmail: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Generate a random unique name for the photo
        val photoName = UUID.randomUUID().toString()
        val photoRef = storageRef.child("$photoName.jpg")

        // Upload photo to Firebase Storage
        photoRef.putFile(photoUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded photo
                photoRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update the user's profile image URL in Firestore
                    updateProfilePicture(userEmail, uri.toString(), onSuccess, onFailure)
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateUserName(email: String, newName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(email).update("name", newName)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


}