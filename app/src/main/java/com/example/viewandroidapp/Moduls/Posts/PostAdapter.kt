package com.example.viewandroidapp.Moduls.Posts

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.Model.Post
import com.example.viewandroidapp.R
import kotlinx.coroutines.delay


class PostAdapter(private val posts: List<Post>, private val isProfileActivity: Boolean) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var model = Model.instance

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicture : ImageView = itemView.findViewById(R.id.profilePicture)
        val nameAndLocation : TextView = itemView.findViewById(R.id.nameAndLocation)
        val timestamp : TextView = itemView.findViewById(R.id.timestamp)
        val postDescription : TextView = itemView.findViewById(R.id.postDescription)
        val postImage : ImageView = itemView.findViewById(R.id.postImage)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        //holder.profilePicture.setImageResource(post.ownerImage)
        holder.nameAndLocation.text = "${post.ownerName} at ${post.location}"
        holder.timestamp.text = post.insertionTime
        holder.postDescription.text = post.description

        // Check if the current user is the owner of the post and in the profile activity
        val sharedPref = holder.itemView.context.getSharedPreferences("user", Context.MODE_PRIVATE)
        val currentUserEmail = sharedPref.getString("ownerEmail", "")
        val isOwner = post.ownerEmail == currentUserEmail
        holder.editButton.visibility = if (isOwner && isProfileActivity) View.VISIBLE else View.GONE

        // Load post photo using Glide
        Glide.with(holder.itemView.context)
            .load(post.photo)
            .into(holder.postImage)

        // Load post owner's profile picture using Glide
        Glide.with(holder.itemView.context)
            .load(post.ownerImage)
            .into(holder.profilePicture)

       // holder.postImage.setImageResource(post.photo)
        Log.d("posts", "Post Adapter : $post")
        //TODO change
        // Handle edit button click
        holder.editButton.setOnClickListener {
            val editPost = posts[position]
            val intent = Intent(holder.itemView.context, EditPostActivity::class.java).apply {
                // Pass necessary data to EditPostActivity
                putExtra("profilePicture", editPost.ownerImage)
                putExtra("nameAndLocation", "${post.ownerName} at ${post.location}")
                putExtra("postDescription", editPost.description)
                putExtra("postImage", editPost.photo)
                putExtra("postId", editPost.id)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}

