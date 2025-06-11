package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.pj.PhotoModel
import com.decentapps.supre.photorecovery.datarecovery.utils.RectangleImageView
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import java.text.DateFormat

class PhotoAdapter(var context: Context, arrayList: ArrayList<PhotoModel>?) :
    RecyclerView.Adapter<PhotoAdapter.MyViewHolder>() {
    var listPhoto: ArrayList<PhotoModel>? = ArrayList()
    var placeholder: BitmapDrawable? = null

    init {
        this.listPhoto = arrayList
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var album_card: RelativeLayout = view.findViewById<View>(R.id.header_album_card) as RelativeLayout
        var ivChecked: ImageView = view.findViewById<View>(R.id.isChecked) as ImageView
        var ivUnChecked: ImageView = view.findViewById<View>(R.id.isUnChecked) as ImageView
        var ivPhoto: RectangleImageView = view.findViewById(R.id.iv_image)
        var tvDate: TextView = view.findViewById<View>(R.id.tvDate) as TextView
        var tvSize: TextView = view.findViewById<View>(R.id.tvSize) as TextView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.card_photo, viewGroup, false)
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val photoModel = listPhoto!![i]
        myViewHolder.tvDate.text = DateFormat.getDateInstance().format(photoModel.lastModified)
        myViewHolder.tvSize.text = Utils.formatSize(photoModel.sizePhoto)
        if (photoModel.isCheck) {
            myViewHolder.ivChecked.visibility = View.VISIBLE
            myViewHolder.ivUnChecked.visibility = View.GONE
        } else {
            myViewHolder.ivChecked.visibility = View.GONE
            myViewHolder.ivUnChecked.visibility = View.VISIBLE
        }
        try {
//            myViewHolder.ivPhoto.cornerRadius=20.0f
            val with = Glide.with(this.context)
            ((with.load("file://" + photoModel.pathPhoto)
                .diskCacheStrategy(DiskCacheStrategy.ALL) as RequestBuilder<*>)
                .priority(Priority.HIGH).centerCrop().error(R.mipmap.ic_launcher).placeholder(
                    placeholder as Drawable?
                ) as RequestBuilder<*>).into(myViewHolder.ivPhoto)
        } catch (e: Exception) {
            val context2 = this.context
            Toast.makeText(context2, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }
        myViewHolder.album_card.setOnClickListener(View.OnClickListener {
            if (photoModel.isCheck) {
                myViewHolder.ivChecked.visibility = View.GONE
                myViewHolder.ivUnChecked.visibility = View.VISIBLE
                photoModel.isCheck = false
                return@OnClickListener
            }
            myViewHolder.ivChecked.visibility = View.VISIBLE
            myViewHolder.ivUnChecked.visibility = View.GONE
            photoModel.isCheck = true
        })
    }

    override fun getItemCount(): Int {
        return listPhoto!!.size
    }

    val selectedItem: ArrayList<PhotoModel>
        get() {
            val arrayList = ArrayList<PhotoModel>()
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
