package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.utils.SquareImageView
import java.io.File

class RecoveredFilesAdapter(
    var context: Context,
    var files: List<File>,
    var listener: MainListener
) : RecyclerView.Adapter<RecoveredFilesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.files_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        Glide.with(context).load(file).into(holder.img)
        holder.itemView.setOnClickListener { v: View? -> listener.begin(position) }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: SquareImageView = itemView.findViewById(R.id.img)
    }
}
