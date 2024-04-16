package com.example.viewandroidapp.Model

import android.content.Context
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.viewandroidapp.Base.MyApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["ownerEmail"],
        ),
        ForeignKey(
            entity = Post::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
        )
    ]
)data class Comment (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val ownerEmail: String,
    val postId : Long,
    val comment: String,
    val insertionTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
        Date()),
    val lastUpdateTime: Long? = null
){

    companion object {

        var lastUpdated: Long
            get() {
                return MyApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(User.GET_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(User.GET_LAST_UPDATED, value)?.apply()
            }


        const val ID_KEY = "id"
        const val OWNER_EMAIL_KEY = "ownerEmail"
        const val POST_ID_KEY = "postId"
        const val COMMENT_KEY = "comment"
        const val LAST_UPDATED = "lastUpdateTime"
        const val GET_LAST_UPDATED = "get_last_updated"
    }
}