package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.pj.ImageData
import com.decentapps.supre.photorecovery.datarecovery.ui.activity.ScannerActivity

class AdapterImage(var context: Context, var alImageData: ArrayList<ImageData>?) : BaseAdapter() {
    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return alImageData!!.size
    }

    override fun getItem(position: Int): Any {
        return alImageData!![position]
        //        return null;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
        //        return 0;
    }

    override fun getView(position: Int, view: View, viewGroup: ViewGroup): View {
        var view = view
        val imageData = alImageData!![position]
        view = inflater!!.inflate(R.layout.adapter_image, null)
        val ivPic = view.findViewById<ImageView>(R.id.ivPic)
        val cbPic = view.findViewById<CheckBox>(R.id.cbPic)

        if (imageData.selection) {
            cbPic.visibility = View.VISIBLE
        } else {
            cbPic.visibility = View.GONE
        }
        view.tag = viewGroup
        try {
            Glide.with(context).load(alImageData?.get(position)?.filePath)
                .placeholder(R.drawable.empty).centerCrop().into(ivPic)
        } catch (e: Exception) {
            //do nothing
            Toast.makeText(context, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }

        ivPic.setOnClickListener {
            if (imageData.selection) {
                imageData.setSelction(false)
                notifyDataSetChanged()
                ScannerActivity.toolbar?.setTitle(this@AdapterImage.selectedItem.size.toString() + " items selected")
            } else {
                imageData.setSelction(true)
                notifyDataSetChanged()
                ScannerActivity.toolbar?.setTitle(this@AdapterImage.selectedItem.size.toString() + " items selected")
            }

            if (this@AdapterImage.selectedItem.size > 0) {
                ScannerActivity.toolbar?.visibility = View.VISIBLE
            } else {
                ScannerActivity.toolbar?.visibility = View.GONE
            }
        }
        return view
    }

    val selectedItem: ArrayList<ImageData>
        get() {
            val arrayList: ArrayList<ImageData> = ArrayList<ImageData>()
            if (this.alImageData != null) {
                for (i in alImageData!!.indices) {
                    if (alImageData!![i].selection) {
                        arrayList.add(alImageData!![i])
                    }
                }
            }
            return arrayList
        }

    fun setAllImagesUnseleted() {
        if (this.alImageData != null) {
            for (i in alImageData!!.indices) {
                if (alImageData!![i].selection) {
                    alImageData!![i].setSelction(false)
                }
            }
        }
    }

    companion object {
        private var inflater: LayoutInflater? = null
    }
}
