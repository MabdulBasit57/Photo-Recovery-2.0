package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.pj.VideoModel
import com.decentapps.supre.photorecovery.datarecovery.ui.activity.VideoActivity
import com.decentapps.supre.photorecovery.datarecovery.utils.SquareImageView

class SectionListVideoAdapter(
    var mContext: Context,
    private val itemsList: ArrayList<VideoModel>,
    var postion: Int
) : RecyclerView.Adapter<SectionListVideoAdapter.SingleItemRowHolder>() {
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
                .inflate(R.layout.item_image_video, null as ViewGroup?)
        )
    }

    override fun onBindViewHolder(singleItemRowHolder: SingleItemRowHolder, i: Int) {
        val videoModel = itemsList[i]
        try {
            val with = Glide.with(this.mContext)
            ((with.load("file://" + videoModel.pathPhoto)
                .diskCacheStrategy(DiskCacheStrategy.ALL) as RequestBuilder<*>)
                .priority(Priority.HIGH).centerCrop()
                .error(R.mipmap.ic_launcher) as RequestBuilder<*>).into(singleItemRowHolder.itemImage)
        } catch (e: Exception) {
            val context = this.mContext
            Toast.makeText(context, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }
        singleItemRowHolder.itemImage.setOnClickListener { view: View? ->
            val intent = Intent(this@SectionListVideoAdapter.mContext, VideoActivity::class.java)
            intent.putExtra("value", this@SectionListVideoAdapter.postion)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return this.size
    }

    inner class SingleItemRowHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemImage: SquareImageView = view.findViewById<View>(R.id.ivImage) as SquareImageView
    }
}
