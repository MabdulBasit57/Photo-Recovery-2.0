package com.recover.photo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.recover.photo.R
import com.recover.photo.pj.AudioModel
import com.recover.photo.ui.activity.AudioActivity
import com.recover.photo.utils.Utils

class SectionListAudioAdapter(
    var mContext: Context,
    private val itemsList: ArrayList<AudioModel>,
    var postion: Int
) : RecyclerView.Adapter<SectionListAudioAdapter.SingleItemRowHolder>() {
    var size: Int = 0

    init {
        if (itemsList.size >= 5) {
            this.size = 5
        } else {
            this.size = itemsList.size
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleItemRowHolder {
        return SingleItemRowHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_audio_album, null as ViewGroup?)
        )
    }

    override fun onBindViewHolder(singleItemRowHolder: SingleItemRowHolder, i: Int) {
        singleItemRowHolder.tvTitle.text = Utils.getFileTitle(
            itemsList[i].pathPhoto
        )
        singleItemRowHolder.album_card.setOnClickListener { view: View? ->
            val intent = Intent(this@SectionListAudioAdapter.mContext, AudioActivity::class.java)
            intent.putExtra("value", this@SectionListAudioAdapter.postion)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return this.size
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var album_card: CardView = view.findViewById<View>(R.id.album_card) as CardView
        var tvTitle: TextView = view.findViewById<View>(R.id.tvTitle) as TextView
    }
}
