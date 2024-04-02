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
    @Query("SELECT * FROM Post WHERE ownerEmail = :ownerEmail")
    fun getPostsByOwnerEmail(ownerEmail: String): List<Post>
    @Query("SELECT * FROM Post WHERE ownerEmail = :ownerEmail ORDER BY insertionTime DESC")
    fun getLatestPostsByOwnerEmail(ownerEmail: String): List<Post>
    @Query("SELECT COUNT(*) FROM Post WHERE ownerEmail = :ownerEmail")
    fun getCountByOwnerEmail(ownerEmail: String): Int
    @Query("SELECT * FROM Post WHERE location = :location")
    fun getPostsByLocation(location: String): List<Post>
    @Query("SELECT * FROM Post WHERE location = :location ORDER BY insertionTime DESC")
    fun getLatestPostsByLocation(location: String, limit: Int): List<Post>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePost(post: Post)
    @Query("DELETE FROM Post WHERE id = :postId")
    fun deletePostById(postId: Long)
}