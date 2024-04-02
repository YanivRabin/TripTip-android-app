package com.example.viewandroidapp.dao

import com.example.viewandroidapp.Model.Comment
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CommentDao {
    @Query("SELECT * FROM Comment WHERE ownerEmail = :ownerEmail")
    fun getCommentsByOwnerEmail(ownerEmail: String): List<Comment>
    @Query("SELECT COUNT(*) FROM Comment WHERE ownerEmail = :ownerEmail")
    fun getCommentCountByOwnerEmail(ownerEmail: String): Int
    @Query("SELECT * FROM Comment ORDER BY insertionTime DESC LIMIT :limit")
    fun getLatestComments(limit: Int): List<Comment>
    @Query("SELECT * FROM Comment WHERE postId = :postId ORDER BY insertionTime DESC")
    fun getLatestCommentsByPostId(postId: Long): List<Comment>
    @Query("SELECT * FROM Comment WHERE id = :commentId")
    fun getCommentById(commentId: Long): Comment?
    @Query("SELECT * FROM Comment WHERE postId = :postId")
    fun getCommentsByPostId(postId: Long): List<Comment>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: Comment)
    @Update
    fun updateComment(comment: Comment)
    @Query("DELETE FROM Comment WHERE id = :commentId")
    fun deleteCommentById(commentId: Long)
}