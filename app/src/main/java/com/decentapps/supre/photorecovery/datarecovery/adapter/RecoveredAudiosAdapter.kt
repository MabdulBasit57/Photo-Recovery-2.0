package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.decentapps.supre.photorecovery.datarecovery.R
import java.io.File
import java.text.SimpleDateFormat

class RecoveredAudiosAdapter(
    var context: Context,
    var files: List<File>,
    var listener: MainListener
) : RecyclerView.Adapter<RecoveredAudiosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.audios_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.audioName.text = file.name

        val uri = Uri.fromFile(file)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millSecond = durationStr!!.toInt()
        @SuppressLint("SimpleDateFormat") val fmt = SimpleDateFormat("mm:ss")
        val time = fmt.format(millSecond)
        holder.audioTime.text = time
        holder.card.setOnClickListener { v: View? -> listener.begin(position) }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var audioName: TextView = itemView.findViewById(R.id.audioName)
        var audioTime: TextView = itemView.findViewById(R.id.audioTime)
        var card: CardView = itemView.findViewById(R.id.card)
    }
}
