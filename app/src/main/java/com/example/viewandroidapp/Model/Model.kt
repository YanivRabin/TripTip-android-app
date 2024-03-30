package com.example.viewandroidapp.Model

import android.app.appsearch.BatchResultCallback
import android.os.Looper
import androidx.core.os.HandlerCompat
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


    fun insertUser(user: User ,callback: () -> Unit){
        executor.execute{
            database.userDao().insertUser(user)
            mainHandler.post{
                callback()
            }
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

    fun refreshPostsByLocation(location: String, callback: (List<Post>) -> Unit) {
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
                val postsByLocation = database.postDao().getPostsByLocation(location)
                mainHandler.post {
                    callback(postsByLocation)
                    }
                }
        }
    }


    fun getPostsByOwnerEmail(email: String ,callback: (List<Post>) -> Unit){
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


    fun getLatestPosts(callback: (List<Post>) -> Unit){
        executor.execute{
            val posts = database.postDao().getLatestPosts()
            mainHandler.post{
                callback(posts)
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

    fun getPostsByInsertionTime(startTime: String, endTime: String, callback: (List<Post>) -> Unit){
        executor.execute{
            val posts = database.postDao().getPostsByInsertionTime(startTime, endTime)
            mainHandler.post{
                callback(posts)
            }
        }
    }

    fun insertPost(post: Post ,callback: () -> Unit){
        executor.execute{
            database.postDao().insertPost(post)
            mainHandler.post{
                callback()
            }
        }
    }

    fun updatePost(post: Post, callback: () -> Unit){
        executor.execute{
            database.postDao().updatePost(post)
            mainHandler.post{
                callback()
            }
        }
    }


    fun deletePostById(id: Long,callback: () -> Unit){
        executor.execute{
            database.postDao().deletePostById(id)
            mainHandler.post{
                callback()
            }
        }
    }



    //endregion

    //region Comment functions using ROOM
    fun getAllComments(callback: (List<Comment>) -> Unit){
        executor.execute{
            val comments = database.commentDao().getAllComments()
            mainHandler.post{
                callback(comments)
            }
        }
    }

    fun getCommentsByOwnerEmail(ownerEmail: String, callback: (List<Comment>) -> Unit) {
        executor.execute {
            val comments = database.commentDao().getCommentsByOwnerEmail(ownerEmail)
            mainHandler.post {
                callback(comments)
            }
        }
    }

    fun getCommentCountByOwnerEmail(ownerEmail: String, callback: (Int) -> Unit) {
        executor.execute {
            val count = database.commentDao().getCommentCountByOwnerEmail(ownerEmail)
            mainHandler.post {
                callback(count)
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

    fun getCommentsByIdRange(startId: Long, endId: Long, callback: (List<Comment>) -> Unit) {
        executor.execute {
            val comments = database.commentDao().getCommentsByIdRange(startId, endId)
            mainHandler.post {
                callback(comments)
            }
        }
    }

    fun getCommentsByInsertionTime(startTime: String, endTime: String, callback: (List<Comment>) -> Unit) {
        executor.execute {
            val comments = database.commentDao().getCommentsByInsertionTime(startTime, endTime)
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
        executor.execute {
            database.commentDao().insertComment(comment)
            mainHandler.post {
                callback()
            }
        }
    }

    fun updateComment(comment: Comment, callback: () -> Unit) {
        executor.execute {
            database.commentDao().updateComment(comment)
            mainHandler.post {
                callback()
            }
        }
    }

    fun updateCommentText(commentId: Long, newComment: String, callback: () -> Unit) {
        executor.execute {
            database.commentDao().updateCommentText(commentId, newComment)
            mainHandler.post {
                callback()
            }
        }
    }

    fun updateInsertionTime(commentId: Long, newInsertionTime: String, callback: () -> Unit) {
        executor.execute {
            database.commentDao().updateInsertionTime(commentId, newInsertionTime)
            mainHandler.post {
                callback()
            }
        }
    }

    fun deleteCommentById(commentId: Long, callback: () -> Unit) {
        executor.execute {
            database.commentDao().deleteCommentById(commentId)
            mainHandler.post {
                callback()
            }
        }
    }


    //endregion

}