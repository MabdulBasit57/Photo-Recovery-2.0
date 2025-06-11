package com.decentapps.supre.photorecovery.datarecovery.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.pj.AlbumAudio

class AlbumsAudioAdapter(
    var context: Context,
    arrayList: ArrayList<AlbumAudio>,
    onClickItemListener: OnClickItemListener
) : RecyclerView.Adapter<AlbumsAudioAdapter.MyViewHolder>() {
    var al_menu: ArrayList<AlbumAudio> = ArrayList()
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
    /*    var recycler_view_list: RecyclerView =
            view.findViewById<View>(R.id.recycler_view_list) as RecyclerView*/
        var tv_foldersize: TextView = view.findViewById<View>(R.id.tvDate) as TextView

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onClickItemListener.onClickItem(adapterPosition)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.card_audio, viewGroup, false),
            this.mOnClickItemListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val textView = myViewHolder.tv_foldersize
        textView.text = al_menu[i].listPhoto?.size.toString() + " Audio"
/*        val sectionListAudioAdapter = al_menu[i].listPhoto?.let {
            SectionListAudioAdapter(
                this.context,
                it, i
            )
        }
        myViewHolder.recycler_view_list.layoutManager = GridLayoutManager(this.context, 1)
        myViewHolder.recycler_view_list.addItemDecoration(
            GridSpacingItemDecoration(
                1, dpToPx(
                    this.context, 10
                ), true
            )
        )
        myViewHolder.recycler_view_list.adapter = sectionListAudioAdapter*/
    }

    override fun getItemCount(): Int {
        return al_menu.size
    }

/*    inner class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : ItemDecoration() {
        override fun getItemOffsets(
            rect: Rect,
            view: View,
            recyclerView: RecyclerView,
            state: RecyclerView.State
        ) {
            val childAdapterPosition = recyclerView.getChildAdapterPosition(view)
            val i = this.spanCount
            val i2 = childAdapterPosition % i
            if (this.includeEdge) {
                val i3 = this.spacing
                rect.left = i3 - ((i2 * i3) / i)
                rect.right = ((i2 + 1) * i3) / i
                if (childAdapterPosition < i) {
                    rect.top = i3
                }
                rect.bottom = this.spacing
                return
            }
            val i4 = this.spacing
            rect.left = (i2 * i4) / i
            rect.right = i4 - (((i2 + 1) * i4) / i)
            if (childAdapterPosition >= i) {
                rect.top = i4
            }
        }
    }

    private fun dpToPx(context2: Context, i: Int): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                i.toFloat(),
                context2.resources.displayMetrics
            )
        )
    }*/
}

