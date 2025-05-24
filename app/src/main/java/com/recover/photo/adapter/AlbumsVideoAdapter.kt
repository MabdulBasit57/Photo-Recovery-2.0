package com.recover.photo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recover.photo.R
import com.recover.photo.pj.AlbumVideo

class AlbumsVideoAdapter(
    var context: Context,
    arrayList: ArrayList<AlbumVideo>,
    onClickItemListener: OnClickItemListener
) : RecyclerView.Adapter<AlbumsVideoAdapter.MyViewHolder>() {
    var al_menu: ArrayList<AlbumVideo> = ArrayList()
    var mOnClickItemListener: OnClickItemListener

    interface OnClickItemListener {
        fun onClickItem(i: Int)
    }

    init {
        this.al_menu = arrayList
        this.mOnClickItemListener = onClickItemListener
    }

    inner class MyViewHolder(view: View, var onClickItemListener: OnClickItemListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var recycler_view_list: RecyclerView =
            view.findViewById<View>(R.id.recycler_view_list) as RecyclerView
        var tv_foldersize: TextView = view.findViewById<View>(R.id.tv_folder2) as TextView

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onClickItemListener.onClickItem(adapterPosition)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_album_new, viewGroup, false), this.mOnClickItemListener
        )
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val textView = myViewHolder.tv_foldersize
        textView.text = al_menu[i].listPhoto?.size.toString() + " Videos"
        val sectionListPhotoDataAdapter = al_menu[i].listPhoto?.let {
            SectionListVideoAdapter(
                this.context,
                it, i
            )
        }
        myViewHolder.recycler_view_list.setHasFixedSize(true)
        myViewHolder.recycler_view_list.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        myViewHolder.recycler_view_list.adapter = sectionListPhotoDataAdapter
    }

    override fun getItemCount(): Int {
        return al_menu.size
    }
}
