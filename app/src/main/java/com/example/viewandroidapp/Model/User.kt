package com.example.viewandroidapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class User (
    @PrimaryKey val email: String,
    val password: String,
    val name: String,
    val profileImage: String,
    val token: String,
    val insertionTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
        Date()
    )
)