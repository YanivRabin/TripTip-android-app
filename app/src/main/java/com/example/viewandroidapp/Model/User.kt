package com.example.viewandroidapp.Model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.viewandroidapp.Base.MyApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class User (
    @PrimaryKey val email: String = "",
    val password: String = "",
    val name: String = "",
    val profileImage: String = "",
    val token: String = "",
    val insertionTime: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
        Date()
    ),
    val lastUpdateTime: Long? = null
)
{
    // No-argument constructor
    constructor() : this("", "", "", "", "", "")



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


        const val EMAIL_KEY = "email"
        const val PASSWORD_KEY = "password"
        const val NAME_KEY = "name"
        const val PROFILE_IMAGE_KEY = "profileImage"
        const val TOKEN_KEY = "token"
        const val INSERTION_TIME = "insertionTime"
        const val LAST_UPDATED = "lastUpdateTime"
        const val GET_LAST_UPDATED = "get_last_updated"
    }
}
