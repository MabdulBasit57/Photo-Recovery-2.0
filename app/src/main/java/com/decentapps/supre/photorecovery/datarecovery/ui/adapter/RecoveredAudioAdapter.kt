package com.decentapps.supre.photorecovery.datarecovery.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.decentapps.supre.photorecovery.datarecovery.R
import java.io.File

class RecoveredAudioAdapter(
    val context: Context,
    private val audioFiles: List<File>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RecoveredAudioAdapter.AudioViewHolder>() {

    // Interface for item click listener
    interface OnItemClickListener {
        fun onItemClick(file: File, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio_album, parent, false) // Use the custom layout
        return AudioViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audioFile = audioFiles[position]
        holder.bind(audioFile)
    }

    override fun getItemCount(): Int = audioFiles.size

    class AudioViewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        fun bind(audioFile: File) {
            // Bind the audio file name to the TextView
            tvTitle.text = audioFile.name

            // Set click listener on the item view
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(audioFile, adapterPosition)
            }
        }
    }
}