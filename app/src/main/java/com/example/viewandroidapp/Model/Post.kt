package com.example.viewandroidapp.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["ownerEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerEmail")]
)
data class Post (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ownerEmail: String,
    val description: String,
    val photo: String,
    val location: String,
    val insertionTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
        Date()
    ),
)
