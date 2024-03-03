package com.example.viewandroidapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}