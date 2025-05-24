package com.recover.photo.ui.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
//import com.google.android.gms.ads.AdView
import com.recover.photo.R
import com.recover.photo.adapter.AlbumsVideoAdapter

class AlbumVideoActivity : AppCompatActivity(), AlbumsVideoAdapter.OnClickItemListener {
    var adapter: AlbumsVideoAdapter? = null
    var recyclerView: RecyclerView? = null

    override fun onClickItem(i: Int) {
        val intent = Intent(applicationContext, VideoActivity::class.java)
        intent.putExtra("value", i)
        startActivity(intent)
    }


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_album)
     /*   val mAdView = findViewById<AdView>(R.id.madView)
        Utils.showBannerAd(mAdView)*/
        intView()
        intData()
        val titleToolbar = findViewById<TextView>(R.id.titleToolbar)
        val backToolbar = findViewById<ImageView>(R.id.backToolbar)
        backToolbar.setOnClickListener {
            onBackPressed()
        }
        titleToolbar.text="Scanned Videos"
    }

    fun intView() {
        this.recyclerView = findViewById<View>(R.id.gv_folder) as RecyclerView
        recyclerView?.layoutManager = GridLayoutManager(this, 1)
        recyclerView?.addItemDecoration(GridSpacingItemDecoration(1, dpToPx(2), true))
        recyclerView?.itemAnimator = DefaultItemAnimator()
    }

    fun intData() {
        this.adapter = AlbumsVideoAdapter(this, ScanImagesActivty.mAlbumVideo, this)
        recyclerView?.adapter = this.adapter
    }

    inner class GridSpacingItemDecoration(
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

    private fun dpToPx(i: Int): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                i.toFloat(),
                resources.displayMetrics
            )
        )
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == 16908332) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    public override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }
}
