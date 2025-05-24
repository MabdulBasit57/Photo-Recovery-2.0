package com.recover.photo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.recover.photo.R
import com.recover.photo.utils.SquareImageView
import java.io.File

class RecoveredFileAdapter(
    val context:Context,
    private val files: List<File>,
    private val onItemClickListener: OnItemClickListener // Add this parameter
) : RecyclerView.Adapter<RecoveredFileAdapter.FileViewHolder>() {

    // Define an interface for item click listener
    interface OnItemClickListener {
        fun onItemClick(file: File, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.files_view, parent, false)
        return FileViewHolder(view, onItemClickListener) // Pass the listener to ViewHolder
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position],context)
    }

    override fun getItemCount(): Int = files.size

    class FileViewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener // Add this parameter
    ) : RecyclerView.ViewHolder(itemView) {

        private val img: SquareImageView = itemView.findViewById(R.id.img)

        fun bind(file: File,context: Context) {
            Glide.with(context).load(file).into(img)
            // Set click listener on the item view
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(file, adapterPosition)
            }
        }
    }
}