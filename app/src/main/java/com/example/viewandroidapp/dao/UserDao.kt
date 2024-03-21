package com.example.viewandroidapp.dao

import com.example.viewandroidapp.Model.User
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT name FROM User WHERE email = :email")
    suspend fun getUserName(email: String): String
    @Query("SELECT password FROM User WHERE email = :email")
    suspend fun getUserPassword(email: String): String
    @Query("SELECT profileImage FROM User WHERE email = :email")
    suspend fun getProfileImage(email: String): String
    @Query("select * from User")
    suspend fun getAll(): List<User>
    @Query("select * from User where email = :email")
    suspend fun getUserByEmail(email: String): User
    @Insert
    suspend fun insertUser(user: User)
    @Update
    suspend fun updateUser(user: User)
    @Delete
    suspend fun deleteUser(user: User)
    @Query("DELETE FROM User WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)
}