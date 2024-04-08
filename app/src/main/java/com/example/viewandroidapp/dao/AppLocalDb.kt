package com.example.viewandroidapp.dao

import com.example.viewandroidapp.Base.MyApplication
import com.example.viewandroidapp.Model.Comment
import com.example.viewandroidapp.Model.Post
import com.example.viewandroidapp.Model.User
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Post::class, Comment::class], version = 3)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}
object AppLocalDb {
    val db: AppLocalDbRepository by lazy {
        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )

            .fallbackToDestructiveMigration()
            .build()
    }
}
