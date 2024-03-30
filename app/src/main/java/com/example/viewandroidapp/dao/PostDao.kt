package com.example.viewandroidapp.dao

import androidx.room.Dao
import com.example.viewandroidapp.Model.Post
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostDao {
    @Query("SELECT * FROM Post WHERE id = :postId")
    fun getPostById(postId: Long): Post
    @Query("SELECT * FROM Post ORDER BY insertionTime DESC")
    fun getLatestPosts(): List<Post>
    @Query("SELECT * FROM Post WHERE ownerEmail = :ownerEmail")
    fun getPostsByOwnerEmail(ownerEmail: String): List<Post>
    @Query("SELECT COUNT(*) FROM Post WHERE ownerEmail = :ownerEmail")
    fun getCountByOwnerEmail(ownerEmail: String): Int
    @Query("SELECT * FROM Post WHERE insertionTime BETWEEN :startTime AND :endTime")
    fun getPostsByInsertionTime(startTime: String, endTime: String): List<Post>
    @Query("SELECT * FROM Post WHERE location = :location")
    fun getPostsByLocation(location: String): List<Post>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePost(post: Post)
    @Query("DELETE FROM Post WHERE id = :postId")
    fun deletePostById(postId: Long)
}