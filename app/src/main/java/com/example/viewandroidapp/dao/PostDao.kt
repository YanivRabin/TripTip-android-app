package com.example.viewandroidapp.dao

import androidx.room.Dao
import com.example.viewandroidapp.Model.Post
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostDao {
    @Query("SELECT * FROM Post")
    fun getAllPosts(): List<Post>
    @Query("SELECT * FROM Post WHERE id = :postId")
    fun getPostById(postId: Long): Post
    @Query("SELECT location FROM Post WHERE id = :postId")
    fun getLocationByPostId(postId: Long): String
    @Query("SELECT * FROM Post ORDER BY insertionTime DESC")
    fun getLatestPosts(): List<Post>
    @Query("SELECT * FROM Post ORDER BY insertionTime ASC")
    fun getOldestPosts(): List<Post>
    @Query("SELECT * FROM Post WHERE ownerEmail = :ownerEmail")
    fun getPostsByOwnerEmail(ownerEmail: String): List<Post>
    @Query("SELECT COUNT(*) FROM Post WHERE ownerEmail = :ownerEmail")
    fun getCountByOwnerEmail(ownerEmail: String): Int
    @Query("SELECT * FROM Post WHERE insertionTime BETWEEN :startTime AND :endTime")
    fun getPostsByInsertionTime(startTime: String, endTime: String): List<Post>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post)
    @Update
    fun updatePost(post: Post)
    @Query("UPDATE Post SET description = :newDescription WHERE id = :postId")
    fun updatePostDescription(postId: Long, newDescription: String)
    @Query("UPDATE Post SET photo = :newPhotoUrl WHERE id = :postId")
    fun updatePostPhoto(postId: Long, newPhotoUrl: String)
    @Query("UPDATE Post SET description = :newDescription, photo = :newPhotoUrl WHERE id = :postId")
    fun updatePostDescriptionAndPhoto(postId: Long, newDescription: String, newPhotoUrl: String)
    @Query("UPDATE Post SET location = :newLocation WHERE id = :postId")
    fun updatePostLocation(postId: Long, newLocation: String)
    @Query("DELETE FROM Post WHERE id = :postId")
    fun deletePostById(postId: Long)
}