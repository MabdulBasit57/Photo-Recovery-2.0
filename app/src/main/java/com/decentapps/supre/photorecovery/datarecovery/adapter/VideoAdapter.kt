package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.pj.VideoModel
import com.decentapps.supre.photorecovery.datarecovery.utils.SquareImageView
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import java.text.DateFormat

class VideoAdapter(var context: Context, arrayList: ArrayList<VideoModel>?) :
    RecyclerView.Adapter<VideoAdapter.MyViewHolder>() {
    var listPhoto: ArrayList<VideoModel>? = ArrayList()

    init {
        this.listPhoto = arrayList
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var album_card: RelativeLayout = view.findViewById<View>(R.id.album_card) as RelativeLayout
        var cbSelected: AppCompatCheckBox =
            view.findViewById<View>(R.id.cbSelected) as AppCompatCheckBox
        var ivPhoto: SquareImageView = view.findViewById<View>(R.id.iv_image) as SquareImageView
        var tvDate: TextView = view.findViewById<View>(R.id.tvDate) as TextView
        var tvSize: TextView = view.findViewById<View>(R.id.tvSize) as TextView
        var tvType: TextView = view.findViewById<View>(R.id.tvType) as TextView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.card_video, viewGroup, false)
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val videoModel = listPhoto!![i]
        val textView = myViewHolder.tvDate
        textView.text = DateFormat.getDateInstance()
            .format(videoModel.lastModified) + "  " + videoModel.timeDuration
        myViewHolder.cbSelected.isChecked = videoModel.isCheck
        myViewHolder.tvSize.text = Utils.formatSize(videoModel.sizePhoto)
        myViewHolder.tvType.text = videoModel.typeFile
        try {
            val with = Glide.with(this.context)
            ((with.load("file://" + videoModel.pathPhoto)
                .diskCacheStrategy(DiskCacheStrategy.ALL) as RequestBuilder<*>).priority
                (Priority.HIGH).centerCrop()
                .error(R.mipmap.ic_launcher) as RequestBuilder<*>).into(myViewHolder.ivPhoto)
        } catch (e: Exception) {
            val context2 = this.context
            Toast.makeText(context2, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }
       /* myViewHolder.album_card.setOnClickListener {
            val intent = Intent(this@VideoAdapter.context, FileInfoActivity::class.java)
            intent.putExtra("ojectVideo", videoModel)
            context.startActivity(intent)
        }*/
        myViewHolder.cbSelected.setOnClickListener {
            if (myViewHolder.cbSelected.isChecked) {
                videoModel.isCheck = true
            } else {
                videoModel.isCheck = false
            }
        }
    }

    override fun getItemCount(): Int {
        return listPhoto!!.size
    }

    val selectedItem: ArrayList<VideoModel>
        get() {
            val arrayList = ArrayList<VideoModel>()
            if (this.listPhoto != null) {
                for (i in listPhoto!!.indices) {
                    if (listPhoto!![i].isCheck) {
                        arrayList.add(listPhoto!![i])
                    }
                }
            }
            return arrayList
        }

    fun setAllImagesUnseleted() {
        if (this.listPhoto != null) {
            for (i in listPhoto!!.indices) {
                if (listPhoto!![i].isCheck) {
                    listPhoto!![i].isCheck = false
                }
            }
            notifyDataSetChanged()
        }
    }

    fun selectAll() {
        if (this.listPhoto != null) {
            for (i in listPhoto!!.indices) {
                listPhoto!![i].isCheck = true
            }
            notifyDataSetChanged()
        }
    }
}
