package com.example.viewandroidapp.dao

import com.example.viewandroidapp.Model.User
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT name FROM User WHERE email = :email")
    fun getUserName(email: String): String
    @Query("SELECT password FROM User WHERE email = :email")
    fun getUserPassword(email: String): String
    @Query("SELECT profileImage FROM User WHERE email = :email")
    fun getProfileImage(email: String): String
    @Query("select * from User")
    fun getAll(): List<User>
    @Query("select * from User where email = :email")
    fun getUserByEmail(email: String): User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)
    @Update
    fun updateUser(user: User)
    @Delete
    fun deleteUser(user: User)
    @Query("DELETE FROM User WHERE email = :email")
    fun deleteUserByEmail(email: String)
}