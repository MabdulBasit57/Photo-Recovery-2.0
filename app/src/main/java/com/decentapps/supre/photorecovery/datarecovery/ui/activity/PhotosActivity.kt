package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.adapter.PhotoAdapter
import com.decentapps.supre.photorecovery.datarecovery.databinding.ActivityPhotosBinding
import com.decentapps.supre.photorecovery.datarecovery.pj.PhotoModel
import com.decentapps.supre.photorecovery.datarecovery.tasks.RecoverPhotosAsyncTask
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils
import java.io.File

class PhotosActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPhotosBinding.inflate(layoutInflater) }
    var adapter: PhotoAdapter? = null
    var int_position: Int = 0
    var mList: ArrayList<PhotoModel> = ArrayList()
    var mRecoverPhotosAsyncTask: RecoverPhotosAsyncTask? = null

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(binding.root)
        intView()
        intData()
    }

    fun intView() {
        binding.customToolbar.backToolbar.setOnClickListener {
            onBackPressed()
        }
        binding.customToolbar.titleToolbar.text="Photo Recovery"
        findViewById<View>(R.id.TOPButtonsDIV).visibility = View.GONE
        binding?.gvFolder?.setItemAnimator(DefaultItemAnimator())
    }

    fun intData() {
        this.int_position = intent.getIntExtra("value", 0)
        if (ScanImagesActivty.mAlbumPhoto != null && ScanImagesActivty.mAlbumPhoto.size > this.int_position) {
            mList.addAll(ScanImagesActivty.mAlbumPhoto[int_position].listPhoto?.clone() as Collection<PhotoModel>)
        }
        this.adapter = PhotoAdapter(this, this.mList)
        binding?.gvFolder?.adapter = this.adapter
        binding?.apply {
            btnAll?.setOnClickListener { view: View? -> this@PhotosActivity.SortBySize(1) }
            btnIcons?.setOnClickListener { view: View? -> this@PhotosActivity.SortBySize(2) }
            btnMedium?.setOnClickListener { view: View? -> this@PhotosActivity.SortBySize(3) }
            btnLarge?.setOnClickListener { view: View? -> this@PhotosActivity.SortBySize(4) }
            checkboxSelectAll?.setOnCheckedChangeListener { compoundButton: CompoundButton?, z: Boolean ->
                if (!z) {
                    adapter?.setAllImagesUnseleted()
                } else {
                    adapter?.selectAll()
                }
            }
            btnDelete?.setOnClickListener { view: View? ->
                val builder = AlertDialog.Builder(this@PhotosActivity)
                builder.setTitle(this@PhotosActivity.getString(R.string.delete_title) as CharSequence)
                builder.setMessage(this@PhotosActivity.getString(R.string.delete_bdy) as CharSequence)
                builder.setPositiveButton(
                    "ok",
                    DialogInterface.OnClickListener { dialogInterface, i -> this@PhotosActivity.deleteActions() })
                builder.setNegativeButton(
                    "Cancle",
                    DialogInterface.OnClickListener { dialogInterface, i -> })
                val create = builder.create()
                if (!this@PhotosActivity.isFinishing) {
                    create.show()
                }
            }
            btnRestore?.setOnClickListener { view: View? ->
                val selectedItem =
                    adapter?.selectedItem
                if (selectedItem?.size == 0) {
                    val photosActivity = this@PhotosActivity
                    Toast.makeText(
                        photosActivity,
                        photosActivity.getString(R.string.can_not_process),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                if (selectedItem != null) {
                    SResIT(selectedItem)
                }
            }
        }

    }


    fun deleteActions() {
        if (adapter?.selectedItem?.size == 0) {
            Toast.makeText(this, getString(R.string.can_not_process), Toast.LENGTH_LONG).show()
            return
        }
        binding?.checkboxSelectAll?.isChecked = false
        this.mRecoverPhotosAsyncTask = adapter?.selectedItem?.let {
            RecoverPhotosAsyncTask(
                this,
                it, true, object : RecoverPhotosAsyncTask.OnRestoreListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onComplete(str: String?) {
                        val it = mList.iterator()
                        while (it.hasNext()) {
                            val next = it.next()
                            if (next.isCheck) {
                                it.remove()
                                ScanImagesActivty.mAlbumPhoto[int_position].listPhoto?.remove(next)
                                File(next.pathPhoto).exists()
                            }
                        }
                        adapter?.setAllImagesUnseleted()
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
        mRecoverPhotosAsyncTask?.execute()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun SortBySize(i: Int) {
        binding?.checkboxSelectAll?.isChecked = false

        val arrayList =
            ScanImagesActivty.mAlbumPhoto[int_position].listPhoto?.clone() as Collection<PhotoModel>
        mList.clear()
        if (i == 1) {
            mList.addAll(arrayList)
        } else if (i == 2) {
            for (o in arrayList) {
                val photoModel = o as PhotoModel
                if (photoModel.sizePhoto <= (10000L)) {
                    mList.add(photoModel)
                }
            }
        } else if (i == 3) {
            for (o in arrayList) {
                val photoModel2 = o as PhotoModel
                val sizePhoto = photoModel2.sizePhoto
                if (sizePhoto > (10000L) && sizePhoto <= (2048000L)) {
                    mList.add(photoModel2)
                }
            }
        } else if (i == 4) {
            for (o in arrayList) {
                val photoModel3 = o as PhotoModel
                if (photoModel3.sizePhoto > (2048000L)) {
                    mList.add(photoModel3)
                }
            }
        }
        adapter?.setAllImagesUnseleted()
        adapter?.notifyDataSetChanged()
        (binding?.gvFolder?.layoutManager as LinearLayoutManager?)?.scrollToPositionWithOffset(0, 0)
    }

    fun SDCardCheck(): Boolean {
        val externalFilesDirs = ContextCompat.getExternalFilesDirs(this, null as String?)
        return externalFilesDirs.size > 1 && externalFilesDirs[0] != null && externalFilesDirs[1] != null
    }

    fun fileSearch() {
        startActivityForResult(Intent("android.intent.action.OPEN_DOCUMENT_TREE"), 100)
    }

    class GridSpacingItemDecoration(
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

    private fun dpToPx(): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
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

    fun SResIT(arrayList: ArrayList<*>) {
        binding?.checkboxSelectAll?.isChecked = false

        this.mRecoverPhotosAsyncTask = adapter?.selectedItem?.let {
            RecoverPhotosAsyncTask(
                this,
                it, object : RecoverPhotosAsyncTask.OnRestoreListener {
                    override fun onComplete(str: String?) {
                        if (str?.isEmpty() == true) {
                            AdUtils.showHomeInterstitialAd(this@PhotosActivity){
                                val intent = Intent(
                                    this@PhotosActivity.applicationContext,
                                    RecoveredImagesActivity::class.java
                                )
                                intent.putExtra("value", arrayList.size)
                                intent.putExtra("type", 0)
                                this@PhotosActivity.startActivity(intent)
                            }
                        } else if (str == "Er1") {
                            AdUtils.showHomeInterstitialAd(this@PhotosActivity){
                                val photosActivity = this@PhotosActivity
                                Toast.makeText(
                                    photosActivity,
                                    photosActivity.getString(R.string.FileDeletedBeforeScan),
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent2 = Intent(
                                    this@PhotosActivity.applicationContext,
                                    ScanImagesActivty::class.java
                                )
                                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                this@PhotosActivity.startActivity(intent2)
                            }
                        }
                        adapter?.setAllImagesUnseleted()
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
        mRecoverPhotosAsyncTask?.execute()
    }
}