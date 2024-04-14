package com.example.viewandroidapp.Model

import android.app.appsearch.BatchResultCallback
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.viewandroidapp.Moduls.Posts.PostViewModel
import com.example.viewandroidapp.dao.AppLocalDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Model private constructor() {

    private val database = AppLocalDb.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    private var firebase = FireBaseModel()

    companion object {
        val instance: Model = Model()
    }

    //region User functions using ROOM

    fun getUserByEmail(email: String, callback: (User) -> Unit) {
        refreshUserByEmail(email){ callback(it) }
    }

    fun refreshUserByEmail(email: String, callback: (User) -> Unit) {
        Log.d("fetchAndDisplayUserName", "start refreshUserByEmail")
        val lastUpdated: Long = User.lastUpdated
        firebase.getUserByEmail(email, lastUpdated) { user ->
            executor.execute {
                Log.d("fetchAndDisplayUserName", "start room: $user")
                database.userDao().insertUser(user)
                Log.d("fetchAndDisplayUserName", "end room : $user")
                User.lastUpdated = user.lastUpdateTime ?: System.currentTimeMillis()
                val userByEmail = database.userDao().getUserByEmail(email)
                callback(userByEmail) }
        }
    }


    fun insertUser(user: User){
        firebase.saveUser(user)
        executor.execute{
            database.userDao().insertUser(user)
        }
    }

    fun updateUser (user: User ,callback: () -> Unit){
        executor.execute{
            database.userDao().updateUser(user)
            mainHandler.post{
                callback()
            }
        }
    }


    // endregion

    //region Post functions using ROOM

    fun getAllPostsByLocation(location: String): LiveData<List<Post>> {
        refreshPostsByLocation(location)
        return database.postDao().getPostsByLocation(location)
    }

    fun refreshPostsByLocation(location: String) {
        val lastUpdated: Long = Post.lastUpdated
        firebase.getPostsByLocation(location, lastUpdated) { posts ->
            executor.execute {
                var time = lastUpdated
                for (post in posts) {
                    database.postDao().insertPost(post)
                    post.lastUpdateTime?.let {
                        if (time < it)
                            time = post.lastUpdateTime ?: System.currentTimeMillis()
                    }

                    }
                Post.lastUpdated = time
                }
        }
    }

    suspend fun getAllPostsByOwnerEmail(email: String): List<Post> {
        Log.d("posts", "getAllPostsByOwnerEmail from room : ${database.postDao().getPostsByOwnerEmail(email)}")
        refreshPostsByOwnerEmail(email)
        delay(2000)
        Log.d("posts", "getAllPostsByOwnerEmail from room : ${database.postDao().getPostsByOwnerEmail(email)}")
        return database.postDao().getPostsByOwnerEmail(email)
    }


    suspend fun refreshPostsByOwnerEmail(email: String){
        Log.d("posts", "refreshPostsByOwnerEmail")
        val lastUpdated: Long = Post.lastUpdated
        firebase.getPostsByUserEmail(email, lastUpdated) { posts ->
            executor.execute {
                var time = lastUpdated
                for (post in posts) {
                    database.postDao().insertPost(post)
                    post.lastUpdateTime?.let {
                        if (time < it)
                            time = post.lastUpdateTime ?: System.currentTimeMillis()
                    }
                }
                Post.lastUpdated = time
                // Retrieve posts from the database after updating
            }
        }
    }




    fun getPostById(id: Long ,callback: (Post) -> Unit){
        executor.execute{
            val post = database.postDao().getPostById(id)
            mainHandler.post{
                callback(post)
            }
        }
    }

    fun getCountByOwnerEmail(email: String ,callback: (Int) -> Unit){
        executor.execute{
            val countOfPosts = database.postDao().getCountByOwnerEmail(email)
            mainHandler.post{
                callback(countOfPosts)
            }
        }
    }

    fun savePost(post: Post, callback: () -> Unit) {
        Log.d("posts", "Saving post: $post")
        firebase.savePost(post, onSuccess = {
            post.id = it
            executor.execute {
                database.postDao().insertPost(post)
                mainHandler.post {
                    callback()
                }
            }
        }){
            Log.e("savePost Exception", "$it")
        }
        //firebase.uploadPhoto(post.photo, "posts_images")
    }

    fun updatePost(post: Post, callback: () -> Unit){
        firebase.updatePost(post, callback){
            executor.execute{
                database.postDao().updatePost(post)
                mainHandler.post{
                    callback()
                }
            }
        }
    }



    fun deletePostById(id: Long,callback: () -> Unit){
        firebase.deletePost(id, callback){
            executor.execute{
                database.postDao().deletePostById(id)
                mainHandler.post{
                    callback()
                }
            }
        }
    }



    //endregion

    //region Comment functions using ROOM

    fun getCommentsByPostId(postId: Long, callback: (List<Comment>) -> Unit) {
        val lastUpdated: Long = Comment.lastUpdated
        firebase.getCommentsByPostId(postId, lastUpdated) { comments ->
            executor.execute {
                var time = lastUpdated
                for (comment in comments) {
                    database.commentDao().insertComment(comment)
                    comment.lastUpdateTime?.let {
                        if (time < it)
                            time = comment.lastUpdateTime ?: System.currentTimeMillis()
                    }
                }
                Comment.lastUpdated = time
                val commentsByPostId = database.commentDao().getCommentsByPostId(postId)
                mainHandler.post {
                    callback(commentsByPostId)
                }
            }
        }

    }

    fun getCommentsByOwnerEmail(ownerEmail: String, callback: (List<Comment>) -> Unit) {
        val lastUpdated: Long = Comment.lastUpdated
        firebase.getCommentsByOwnerEmail(ownerEmail, lastUpdated) { comments ->
            executor.execute {
                var time = lastUpdated
                for (comment in comments) {
                    database.commentDao().insertComment(comment)
                    comment.lastUpdateTime?.let {
                        if (time < it)
                            time = comment.lastUpdateTime ?: System.currentTimeMillis()
                    }
                }
                Comment.lastUpdated = time
                val commentsByOwnerEmail = database.commentDao().getCommentsByOwnerEmail(ownerEmail)
                mainHandler.post {
                    callback(commentsByOwnerEmail)
                }
            }
        }
    }


    fun getLatestComments(limit: Int, callback: (List<Comment>) -> Unit) {
        executor.execute {
            val comments = database.commentDao().getLatestComments(limit)
            mainHandler.post {
                callback(comments)
            }
        }
    }

    fun getCommentById(commentId: Long, callback: (Comment?) -> Unit) {
        executor.execute {
            val comment = database.commentDao().getCommentById(commentId)
            mainHandler.post {
                callback(comment)
            }
        }
    }

    fun insertComment(comment: Comment, callback: () -> Unit) {
        firebase.saveComment(comment, callback) {
            executor.execute {
                database.commentDao().insertComment(comment)
                mainHandler.post {
                    callback()
                }
            }
        }
    }

    fun updateComment(comment: Comment, callback: () -> Unit) {
        firebase.updateComment(comment, callback) {
            executor.execute {
                database.commentDao().updateComment(comment)
                mainHandler.post {
                    callback()
                }
            }
        }
    }


    fun deleteCommentById(commentId: Long, callback: () -> Unit) {
        firebase.deleteComment(commentId, callback) {
            executor.execute {
                database.commentDao().deleteCommentById(commentId)
                mainHandler.post {
                    callback()
                }
            }
        }
    }

    fun uploadPhoto(photoUri: String, folder: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        firebase.uploadPhoto(photoUri, folder,
            onSuccess = { downloadUrl ->
                // On success, call the onSuccess callback with the download URL
                onSuccess(downloadUrl)
            },
            onFailure = { exception ->
                // On failure, call the onFailure callback with the exception
                onFailure(exception)
            }
        )
    }

    fun editPost(postId: String, newDescription: String, photoUri: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebase.editPost(postId, newDescription, photoUri,
            onSuccess = {
                onSuccess() // Invoke the success callback upon successful update
            },
            onFailure = { exception ->
                onFailure(exception) // Pass any errors to the failure callback
            }
        )
    }


    //endregion

}