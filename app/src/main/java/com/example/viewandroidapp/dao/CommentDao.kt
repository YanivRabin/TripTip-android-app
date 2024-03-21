package com.example.viewandroidapp.dao

import com.example.viewandroidapp.Model.Comment
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CommentDao {
    @Query("SELECT * FROM Comment")
    fun getAllComments(): List<Comment>
    @Query("SELECT * FROM Comment WHERE ownerEmail = :ownerEmail")
    fun getCommentsByOwnerEmail(ownerEmail: String): List<Comment>
    @Query("SELECT COUNT(*) FROM Comment WHERE ownerEmail = :ownerEmail")
    fun getCommentCountByOwnerEmail(ownerEmail: String): Int
    @Query("SELECT * FROM Comment ORDER BY insertionTime DESC LIMIT :limit")
    fun getLatestComments(limit: Int): List<Comment>
    @Query("SELECT * FROM Comment WHERE id BETWEEN :startId AND :endId")
    fun getCommentsByIdRange(startId: Long, endId: Long): List<Comment>
    @Query("SELECT * FROM Comment WHERE insertionTime BETWEEN :startTime AND :endTime")
    fun getCommentsByInsertionTime(startTime: String, endTime: String): List<Comment>
    @Query("SELECT * FROM Comment WHERE id = :commentId")
    fun getCommentById(commentId: Long): Comment?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: Comment)
    @Update
    fun updateComment(comment: Comment)
    @Query("UPDATE Comment SET comment = :newComment WHERE id = :commentId")
    fun updateCommentText(commentId: Long, newComment: String)
    @Query("UPDATE Comment SET insertionTime = :newInsertionTime WHERE id = :commentId")
    fun updateInsertionTime(commentId: Long, newInsertionTime: String)
    @Query("DELETE FROM Comment WHERE id = :commentId")
    fun deleteCommentById(commentId: Long)
}