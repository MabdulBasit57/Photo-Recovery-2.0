package com.decentapps.supre.photorecovery.datarecovery.ui.activity

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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.adapter.AlbumsAudioAdapter

class AlbumAudioActivity : AppCompatActivity(), AlbumsAudioAdapter.OnClickItemListener {
    var adapter: AlbumsAudioAdapter? = null
    var recyclerView: RecyclerView? = null

    override fun onClickItem(i: Int) {
        val intent = Intent(applicationContext, AudioActivity::class.java)
        intent.putExtra("value", i)
        startActivity(intent)
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_album)
        intView()
        intData()
        val titleToolbar = findViewById<TextView>(R.id.titleToolbar)
        val backToolbar = findViewById<ImageView>(R.id.backToolbar)
        backToolbar.setOnClickListener {
            onBackPressed()
        }
        titleToolbar.text="Scanned Audios"
    }

    fun intView() {
        this.recyclerView = findViewById<View>(R.id.gv_folder) as RecyclerView
        recyclerView?.itemAnimator = DefaultItemAnimator()
    }

    fun intData() {
        this.adapter = AlbumsAudioAdapter(this, ScanImagesActivty.mAlbumAudio, this)
        recyclerView?.adapter = this.adapter
        val items = findViewById<TextView>(R.id.totalItems)
        val folders = findViewById<TextView>(R.id.totalFolders)
        items.text="${ScanImagesActivty.itemSize}\nItems"
        folders.text="${ScanImagesActivty.mAlbumAudio.size.toString()}\nFolders"
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
        adapter!!.notifyDataSetChanged()
    }
}
