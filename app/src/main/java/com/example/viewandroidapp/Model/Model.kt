package com.example.viewandroidapp.Model

import android.app.appsearch.BatchResultCallback
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import com.example.viewandroidapp.dao.AppLocalDb
import java.util.concurrent.Executors

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
        executor.execute() {
            val user = database.userDao().getUserByEmail(email)
            mainHandler.post {
                callback(user)
            }
        }
    }

    fun refreshUserByEmail(email: String, callback: (User) -> Unit) {
        val lastUpdated: Long = User.lastUpdated
        firebase.getUserByEmail(email, lastUpdated) { user ->
            executor.execute {
                database.userDao().insertUser(user)
                User.lastUpdated = user.lastUpdateTime ?: System.currentTimeMillis()
                val userByEmail = database.userDao().getUserByEmail(email)
                mainHandler.post {
                    callback(userByEmail)
                }
            }
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
/*                val postsByLocation = database.postDao().getPostsByLocation(location)
                mainHandler.post {
                    callback(postsByLocation)
                    }*/
                }
        }
    }


    fun refreshPostsByOwnerEmail(email: String ,callback: (List<Post>) -> Unit){
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
                val postsByOwnerEmail = database.postDao().getPostsByOwnerEmail(email)
                mainHandler.post {
                    callback(postsByOwnerEmail)
                }
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
        firebase.savePost(post, callback){
            executor.execute {
                database.postDao().insertPost(post)
                mainHandler.post {
                    callback()
                }
            }
        }
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


    //endregion

}