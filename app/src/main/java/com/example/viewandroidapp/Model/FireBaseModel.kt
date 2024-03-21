package com.example.viewandroidapp.Model

import Model.Post
import Model.User
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class FireBaseModel {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users") // Reference to the "users" collection in Firestore
    private val storage = FirebaseStorage.getInstance()
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
    fun uploadPhoto(photoUrl: String, type: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        // Generate a unique filename for the photo
        val fileName = "${UUID.randomUUID()}.jpg"

        // Determine the storage reference based on the type
        val storageRef: StorageReference = if (type == "profile_image") {
            // If it's a profile image, store it in the "profile_images" directory
            storage.reference.child("profile_images/$fileName")
        } else {
            // If it's a post image, store it in the "post_images" directory
            storage.reference.child("posts_images/$fileName")
        }

        // Get the URI of the photo
        val photoUri = Uri.parse(photoUrl)

        // Upload the photo to Firebase Storage
        storageRef.putFile(photoUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded photo
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val downloadUrl = downloadUri.toString()
                    onSuccess(downloadUrl) // Pass the download URL to the success callback
                }.addOnFailureListener {
                    onFailure(it) // Pass any errors to the failure callback
                }
            }
            .addOnFailureListener { e ->
                onFailure(e) // Pass any errors to the failure callback
            }
    }
    fun updateUserName(email: String, newName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersCollection.document(email).update("name", newName)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun updateProfileImage(userEmail: String, photoUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Update the profile image URL in the Firestore database
        usersCollection.document(userEmail).update("profileImage", photoUrl)
            .addOnSuccessListener {
                // If the update succeeds, invoke the onSuccess callback
                onSuccess()
            }
            .addOnFailureListener { e ->
                // If the update fails, invoke the onFailure callback and pass the exception
                onFailure(e)
            }
    }

    fun savePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val postsCollection = db.collection("posts")
        val postData = hashMapOf(
            "ownerEmail" to post.ownerEmail,
            "description" to post.description,
            "photo" to post.photo,
            "location" to post.location,
            "insertionTime" to post.insertionTime
        )

        postsCollection.add(postData)
            .addOnSuccessListener { documentReference ->
                Log.d("FireBaseModel", "Post added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error adding post", e)
                onFailure(e)
            }
    }
    fun getPosts(userEmail: String, onSuccess: (List<Post>) -> Unit, onFailure: (Exception) -> Unit) {
        val postsCollection = db.collection("posts")

        // Query posts where ownerEmail matches the provided userEmail
        postsCollection.whereEqualTo("ownerEmail", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postsList = mutableListOf<Post>()

                for (document in querySnapshot.documents) {
                    val post = document.toObject(Post::class.java)
                    post?.let {
                        postsList.add(it)
                    }
                }

                onSuccess(postsList)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

}