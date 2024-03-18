package com.example.viewandroidapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

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
        holder.profilePicture.setImageResource(post.profilePicture)
        holder.nameAndLocation.text = post.nameAndLocation
        holder.timestamp.text = post.timestamp
        holder.postDescription.text = post.postDescription
        holder.postImage.setImageResource(post.postImage)

        // Handle edit button click
        holder.editButton.setOnClickListener {
            val editPost = posts[position]
            val intent = Intent(holder.itemView.context, EditPostActivity::class.java).apply {
                // Pass necessary data to EditPostActivity
                putExtra("profilePicture", editPost.profilePicture)
                putExtra("nameAndLocation", editPost.nameAndLocation)
                putExtra("postDescription", editPost.postDescription)
                putExtra("postImage", editPost.postImage)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}