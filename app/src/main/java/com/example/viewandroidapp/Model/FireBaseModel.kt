package com.example.viewandroidapp.Model


import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class FireBaseModel {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
        const val USERS_COLLECTION_PATH = "users"
        const val COMMENTS_COLLECTION_PATH = "comments"
    }



    //region User functions
    fun saveUser(user: User) {
        // Convert User object into a map
        val userData = hashMapOf(
            "email" to user.email,
            "password" to user.password,
            "name" to user.name,
            "profileImage" to user.profileImage,
            "token" to user.token,
            "insertionTime" to user.insertionTime,
            "lastUpdateTime" to System.currentTimeMillis()
        )

        // Add or update the document in the "users" collection with the email as document ID
        db.collection(USERS_COLLECTION_PATH).document(user.email).set(userData)
            .addOnSuccessListener {
                println("User successfully saved to Firestore")
            }
            .addOnFailureListener { e ->
                println("Error saving user to Firestore: $e")
            }
    }

    fun getUser(email: String, callback: (User?) -> Unit) {
        db.collection(USERS_COLLECTION_PATH).document(email).get()
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

    fun getUserByEmail(email: String, since: Long, callback: (User) -> Unit) {
        Log.d("fetchAndDisplayUserName", "start fireBase: $User.LAST_UPDATED")
        db.collection(USERS_COLLECTION_PATH)
            .whereEqualTo(User.EMAIL_KEY, email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val user = querySnapshot.documents[0].toObject(User::class.java)!!
                Log.d("fetchAndDisplayUserName", "end fireBase: $user")
                callback(user)
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error getting user by email", e)
                callback(User())
            }
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
        db.collection(USERS_COLLECTION_PATH).document(email).update(User.NAME_KEY, newName,
            User.LAST_UPDATED, System.currentTimeMillis())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateProfilePicture(email: String, newProfilePictureUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(USERS_COLLECTION_PATH).document(email).update(User.PROFILE_IMAGE_KEY,
            newProfilePictureUrl, User.LAST_UPDATED, System.currentTimeMillis())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateProfileImage(userEmail: String, photoUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Update the profile image URL in the Firestore database
        db.collection(USERS_COLLECTION_PATH).document(userEmail).
        update(User.PROFILE_IMAGE_KEY, photoUrl, User.LAST_UPDATED, System.currentTimeMillis())
            .addOnSuccessListener {
                // If the update succeeds, invoke the onSuccess callback
                onSuccess()
            }
            .addOnFailureListener { e ->
                // If the update fails, invoke the onFailure callback and pass the exception
                onFailure(e)
            }
    }

    //endregion

    //region Posts functions
    suspend fun getPostsByUserEmail(userEmail: String, since: Long, callback: (List<Post>) -> Unit) {
        Log.d("posts", "start fire base, getPostsByUserEmail")
        db.collection(POSTS_COLLECTION_PATH).whereGreaterThanOrEqualTo(Post.LAST_UPDATED, since)
            .whereEqualTo(Post.EMAIL_KEY, userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postsList = mutableListOf<Post>()

                for (document in querySnapshot.documents) {
                    val post = document.toObject(Post::class.java)
                    if (post != null) {
                        post.id = document.id
                    }
                    post?.let {
                        postsList.add(it)
                    }
                }
                Log.d("posts", "firebase posts : $postsList")
                callback(postsList)
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error getting posts by user email", e)
                callback(emptyList())
            }
    }

    fun getPostsByLocation(location: String, since: Long, callback: (List<Post>) -> Unit) {
        Log.d("posts", "start fire base, getPostsByLocation")
        db.collection(POSTS_COLLECTION_PATH).whereGreaterThanOrEqualTo(Post.LAST_UPDATED, since)
            .whereEqualTo(Post.LOCATION_KEY, location)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val postsList = mutableListOf<Post>()

                for (document in querySnapshot.documents) {
                    val post = document.toObject(Post::class.java)
                    if (post != null) {
                        post.id = document.id
                    }
                    post?.let {
                        postsList.add(it)
                    }
                }
                callback(postsList)
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error getting posts by location", e)
                callback(emptyList())
            }
    }

    fun savePost(post: Post, onSuccess: (documentReference: String) -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("FireBaseModel save post", "Saving post: $post")
        val postsCollection = db.collection(POSTS_COLLECTION_PATH)
        val postData = hashMapOf(
            "ownerEmail" to post.ownerEmail,
            "ownerName" to post.ownerName,
            "ownerImage" to post.ownerImage,
            "description" to post.description,
            "photo" to post.photo,
            "location" to post.location,
            "insertionTime" to post.insertionTime,
            "lastUpdateTime" to post.lastUpdateTime
        )

        postsCollection.add(postData)
            .addOnSuccessListener { documentReference ->
                Log.d("FireBaseModel save post", "Post added with ID: ${documentReference.id}")
                // Update the postId field with the documentReference.id
                postData["postId"] = documentReference.id
                documentReference.set(postData)
                    .addOnSuccessListener {
                        Log.d("FireBaseModel save post", "Post data updated with postId: ${documentReference.id}")
                        // Update the post object with the postId
                        onSuccess(documentReference.id)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FireBaseModel save post", "Error updating post data", e)
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel save post", "Error adding post", e)
                onFailure(e)
            }
    }

    fun deletePost(postId: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).document(postId.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("FireBaseModel", "Post deleted successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error deleting post", e)
                onFailure(e)
            }
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val postsCollection = db.collection(POSTS_COLLECTION_PATH)
        val postData = hashMapOf(
            "ownerEmail" to post.ownerEmail,
            "description" to post.description,
            "photo" to post.photo,
            "location" to post.location,
            "insertionTime" to post.insertionTime,
            "lastUpdateTime" to System.currentTimeMillis()
        )

        postsCollection.document(post.id.toString()).set(postData)
            .addOnSuccessListener {
                Log.d("FireBaseModel", "Post updated successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error updating post", e)
                onFailure(e)
            }
    }

    //endregion

    //region Comments functions

    fun getCommentsByPostId(postId: Long, since: Long, callback: (List<Comment>) -> Unit) {
        db.collection(COMMENTS_COLLECTION_PATH).whereGreaterThanOrEqualTo(Comment.LAST_UPDATED, since)
            .whereEqualTo(Comment.POST_ID_KEY, postId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val commentsList = mutableListOf<Comment>()

                for (document in querySnapshot.documents) {
                    val comment = document.toObject(Comment::class.java)
                    comment?.let {
                        commentsList.add(it)
                    }
                }

                callback(commentsList)
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error getting comments by post ID", e)
                callback(emptyList())
            }
    }

    fun getCommentsByOwnerEmail(ownerEmail: String, since: Long, callback: (List<Comment>) -> Unit) {
        db.collection(COMMENTS_COLLECTION_PATH).whereGreaterThanOrEqualTo(Comment.LAST_UPDATED, since)
            .whereEqualTo(Comment.OWNER_EMAIL_KEY, ownerEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val commentsList = mutableListOf<Comment>()

                for (document in querySnapshot.documents) {
                    val comment = document.toObject(Comment::class.java)
                    comment?.let {
                        commentsList.add(it)
                    }
                }

                callback(commentsList)
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error getting comments by owner email", e)
                callback(emptyList())
            }
    }

    fun saveComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val commentsCollection = db.collection(COMMENTS_COLLECTION_PATH)
        val commentData = hashMapOf(
            "ownerEmail" to comment.ownerEmail,
            "postId" to comment.postId,
            "comment" to comment.comment,
            "insertionTime" to comment.insertionTime
        )

        commentsCollection.add(commentData)
            .addOnSuccessListener { documentReference ->
                Log.d("FireBaseModel", "Comment added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error adding comment", e)
                onFailure(e)
            }
    }

    fun deleteComment(commentId: Long, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(COMMENTS_COLLECTION_PATH).document(commentId.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("FireBaseModel", "Comment deleted successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error deleting comment", e)
                onFailure(e)
            }
    }

    fun updateComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val commentsCollection = db.collection(COMMENTS_COLLECTION_PATH)
        val commentData = hashMapOf(
            "ownerEmail" to comment.ownerEmail,
            "postId" to comment.postId,
            "comment" to comment.comment,
            "insertionTime" to comment.insertionTime,
            "lastUpdateTime" to System.currentTimeMillis()
        )

        commentsCollection.document(comment.id.toString()).set(commentData)
            .addOnSuccessListener {
                Log.d("FireBaseModel", "Comment updated successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FireBaseModel", "Error updating comment", e)
                onFailure(e)
            }
    }
    fun editPost(postId: String, newDescription: String, newPhotoUrl: String, photoUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Upload the new photo to Firebase Storage
        uploadPhoto(photoUri.toString(), "post_image",
            onSuccess = { newPhotoDownloadUrl ->
                // Reference to the post document
                val postRef = db.collection(POSTS_COLLECTION_PATH).document(postId)

                // Create a map with the fields to update
                val updates = hashMapOf<String, Any>(
                    "description" to newDescription,
                    "photo" to newPhotoDownloadUrl, // Use the new photo URL from Storage
                    "lastUpdateTime" to System.currentTimeMillis()
                )

                // Update the post document
                postRef.update(updates)
                    .addOnSuccessListener {
                        Log.d("FireBaseModel", "Post updated successfully")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("FireBaseModel", "Error updating post", e)
                        onFailure(e)
                    }
            },
            onFailure = { e ->
                onFailure(e) // Pass any errors to the failure callback
            }
        )
    }


    //endregion

}

