package com.example.viewandroidapp.Moduls.Posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.viewandroidapp.Model.Post

class PostViewModel: ViewModel() {
    var posts: LiveData<List<Post>>? = null
}