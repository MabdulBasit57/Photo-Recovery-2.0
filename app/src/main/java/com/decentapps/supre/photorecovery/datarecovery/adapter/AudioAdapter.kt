package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.pj.AudioModel
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import java.io.File
import java.text.DateFormat

class AudioAdapter(var context: Context, arrayList: ArrayList<AudioModel>?) :
    RecyclerView.Adapter<AudioAdapter.MyViewHolder>() {
    var listPhoto: ArrayList<AudioModel>? = ArrayList()
    var placeholder: BitmapDrawable? = null

    init {
        this.listPhoto = arrayList
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var album_card: RelativeLayout = view.findViewById<View>(R.id.album_card) as RelativeLayout
        var ivChecked: AppCompatCheckBox =
            view.findViewById<View>(R.id.cbSelect) as AppCompatCheckBox
        var tvDate: TextView = view.findViewById<View>(R.id.tvType) as TextView
        var tvSize: TextView = view.findViewById<View>(R.id.tvSize) as TextView
        var tvTitle: TextView = view.findViewById<View>(R.id.tvTitle) as TextView
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_audio, viewGroup, false)
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val audioModel = listPhoto!![i]
        myViewHolder.tvDate.text = DateFormat.getDateInstance().format(audioModel.lastModified)
        myViewHolder.tvSize.text = Utils.formatSize(audioModel.sizePhoto)
        myViewHolder.tvTitle.text = Utils.getFileTitle(audioModel.pathPhoto)
        myViewHolder.ivChecked.isChecked = audioModel.isCheck
        myViewHolder.ivChecked.setOnClickListener(View.OnClickListener {
            if (audioModel.isCheck) {
                myViewHolder.ivChecked.isChecked = false
                audioModel.isCheck = false
                return@OnClickListener
            }
            myViewHolder.ivChecked.isChecked = true
            audioModel.isCheck = true
        })
        myViewHolder.album_card.setOnClickListener {
            try {
                this@AudioAdapter.openFile(File(audioModel.pathPhoto))
            } catch (unused: Exception) {
            }
        }
    }

    override fun getItemCount(): Int {
        return listPhoto!!.size
    }

    val selectedItem: ArrayList<AudioModel>
        get() {
            val arrayList = ArrayList<AudioModel>()
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

    fun openFile(file: File) {
        val intent = Intent()
        intent.setAction("android.intent.action.VIEW")
        if (file.exists()) {
            if (Build.VERSION.SDK_INT < 24) {
                intent.setDataAndType(Uri.fromFile(file), "audio/*")
            } else {
                try {
                    val context2 = this.context
                    val uriForFile = FileProvider.getUriForFile(
                        context2,
                        context.packageName + ".fileprovider", file
                    )
                    context.grantUriPermission(
                        context.packageName,
                        uriForFile,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    intent.setDataAndType(uriForFile, "audio/*")
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (unused: Exception) {
                    return
                }
            }
            context.startActivity(Intent.createChooser(intent, "Complete action using"))
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
