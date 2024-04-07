package com.example.viewandroidapp.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.viewandroidapp.Base.MyApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context

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
    val ownerEmail: String = "",
    val ownerName: String = "",
    val ownerImage: Int = 0,
    val description: String = "",
    val photo: Int = 0,
    val location: String = "",
    val insertionTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
        Date()),
    val lastUpdateTime: Long? = null){


    // constructor
    constructor(ownerEmail: String, ownerName: String, ownerImage: Int, description: String, photo: Int, location: String) :
            this(0, ownerEmail, ownerName, ownerImage, description, photo, location, SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                System.currentTimeMillis())
    companion object {
        var lastUpdated: Long
            get() {
                return MyApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(GET_LAST_UPDATED, value)?.apply()
            }


        const val ID_KEY = "id"
        const val EMAIL_KEY = "ownerEmail"
        const val DESCRIPTION_KEY = "description"
        const val PHOTO_KEY = "photo"
        const val LOCATION_KEY = "location"
        const val INSERTION_TIME = "insertionTime"
        const val LAST_UPDATED = "lastUpdateTime"
        const val GET_LAST_UPDATED = "get_last_updated"

    }
}

