package com.recover.photo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import com.recover.photo.R
import com.recover.photo.pj.ImageData

class AdapterRestoredImages(var context: Context, var alImageData: ArrayList<ImageData>) :
    BaseAdapter() {
    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return alImageData.size
    }

    override fun getItem(position: Int): Any {
        return alImageData[position]
        //        return null;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
        //        return 0;
    }

    override fun getView(position: Int, vew: View, viewGroup: ViewGroup): View {
        var view = vew
        val imageData = alImageData[position]
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
            GlideApp.with(context).load(alImageData[position].filePath)
                .placeholder(R.drawable.no_image).centerCrop().into(ivPic)
        } catch (e: Exception) {
            //do nothing
            Toast.makeText(context, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
        }

        ivPic.setOnClickListener {
            if (imageData.selection) {
                imageData.setSelction(false)
                notifyDataSetChanged()
                //                    toolbar.setTitle(getSelectedItem().size()+" items selected");
            } else {
                imageData.setSelction(true)
                notifyDataSetChanged()
                //                    toolbar.setTitle(getSelectedItem().size()+" items selected");
            }
        }
        return view
    }

    val selectedItem: ArrayList<ImageData>?
        get() {
            val arrayList: ArrayList<ImageData>?=null
            for (i in alImageData.indices) {
                if (alImageData[i].selection) {
                    arrayList?.add(alImageData[i])
                }
            }
            return arrayList
        }

    fun setAllImagesUnseleted() {
        for (i in alImageData.indices) {
            if (alImageData[i].selection) {
                alImageData[i].setSelction(false)
            }
        }
    }

    companion object {
        private var inflater: LayoutInflater? = null
    }
}
