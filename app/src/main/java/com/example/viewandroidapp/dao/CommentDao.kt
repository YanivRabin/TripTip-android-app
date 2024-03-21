package com.example.viewandroidapp.dao

import com.example.viewandroidapp.Model.Comment
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CommentDao {
    @Query("SELECT * FROM Comment")
    suspend fun getAllComments(): List<Comment>
    @Query("SELECT * FROM Comment WHERE ownerEmail = :ownerEmail")
    suspend fun getCommentsByOwnerEmail(ownerEmail: String): List<Comment>
    @Query("SELECT COUNT(*) FROM Comment WHERE ownerEmail = :ownerEmail")
    suspend fun getCommentCountByOwnerEmail(ownerEmail: String): Int
    @Query("SELECT * FROM Comment ORDER BY insertionTime DESC LIMIT :limit")
    suspend fun getLatestComments(limit: Int): List<Comment>
    @Query("SELECT * FROM Comment WHERE id BETWEEN :startId AND :endId")
    suspend fun getCommentsByIdRange(startId: Long, endId: Long): List<Comment>
    @Query("SELECT * FROM Comment WHERE insertionTime BETWEEN :startTime AND :endTime")
    suspend fun getCommentsByInsertionTime(startTime: String, endTime: String): List<Comment>
    @Query("SELECT * FROM Comment WHERE id = :commentId")
    suspend fun getCommentById(commentId: Long): Comment?
    @Insert
    suspend fun insertComment(comment: Comment)
    @Update
    suspend fun updateComment(comment: Comment)
    @Query("UPDATE Comment SET comment = :newComment WHERE id = :commentId")
    suspend fun updateCommentText(commentId: Long, newComment: String)
    @Query("UPDATE Comment SET insertionTime = :newInsertionTime WHERE id = :commentId")
    suspend fun updateInsertionTime(commentId: Long, newInsertionTime: String)
    @Query("DELETE FROM Comment WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: Long)
}