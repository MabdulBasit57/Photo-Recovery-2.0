package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.adapter.AudioAdapter
import com.decentapps.supre.photorecovery.datarecovery.databinding.ActivityAudioBinding
import com.decentapps.supre.photorecovery.datarecovery.pj.AudioModel
import com.decentapps.supre.photorecovery.datarecovery.tasks.RecoverAudioAsyncTask
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils

class AudioActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAudioBinding.inflate(layoutInflater) }
    var adapter: AudioAdapter? = null
    var int_position: Int = 0
    var mList: ArrayList<AudioModel> = ArrayList()
    var mRecoverPhotosAsyncTask: RecoverAudioAsyncTask? = null

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
        binding.customToolbar.titleToolbar.text="Audio Recovery"
        binding?.gvFolder?.itemAnimator = DefaultItemAnimator()
    }

    fun intData() {
        this.int_position = intent.getIntExtra("value", 0)
        if (ScanImagesActivty.mAlbumAudio != null && ScanImagesActivty.mAlbumAudio.size > this.int_position) {
            mList.addAll(ScanImagesActivty.mAlbumAudio[int_position]?.listPhoto?.clone() as ArrayList<AudioModel>)
        }
        this.adapter = AudioAdapter(this, this.mList)
        binding?.gvFolder?.adapter = this.adapter
        binding?.apply {
            checkboxSelectAll?.setOnCheckedChangeListener { compoundButton, z ->
                if (!z) {
                    adapter?.setAllImagesUnseleted()
                } else {
                    adapter?.selectAll()
                }
            }
            btnDelete?.setOnClickListener {
                val builder = AlertDialog.Builder(this@AudioActivity)
                builder.setTitle(this@AudioActivity.getString(R.string.delete_title) as CharSequence)
                builder.setMessage(this@AudioActivity.getString(R.string.delete_bdy) as CharSequence)
                builder.setPositiveButton(
                    "ok",
                    DialogInterface.OnClickListener { dialogInterface, i -> this@AudioActivity.deleteActions() })
                builder.setNegativeButton(
                    "Cancle",
                    DialogInterface.OnClickListener { dialogInterface, i -> })
                builder.create().show()
            }
            btnRestore?.setOnClickListener(View.OnClickListener {
                val selectedItem =
                    adapter?.selectedItem
                if (selectedItem?.size == 0) {
                    val audioActivity = this@AudioActivity
                    Toast.makeText(
                        audioActivity,
                        audioActivity.getString(R.string.can_not_process),
                        Toast.LENGTH_LONG
                    ).show()
                    return@OnClickListener
                }
                if (selectedItem != null) {
                    this@AudioActivity.SResIT(selectedItem)
                }
            })
        }

    }

    fun SResIT(arrayList: ArrayList<*>) {
        binding?.checkboxSelectAll?.isChecked = false
        this.mRecoverPhotosAsyncTask = adapter?.selectedItem?.let {
            RecoverAudioAsyncTask(
                this,
                it, object : RecoverAudioAsyncTask.OnRestoreListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onComplete(str: String?) {
                        if (str!!.isEmpty()) {
                            AdUtils.showHomeInterstitialAd(this@AudioActivity) {
                                val intent = Intent(
                                    this@AudioActivity.applicationContext,
                                    RecoveredAudiosActivity::class.java
                                )
                                intent.putExtra("value", arrayList.size)
                                intent.putExtra("type", 2)
                                this@AudioActivity.startActivity(intent)
                            }
                        } else if (str == "Er1") {
                            AdUtils.showHomeInterstitialAd(this@AudioActivity) {
                                val audioActivity = this@AudioActivity
                                Toast.makeText(
                                    audioActivity,
                                    audioActivity.getString(R.string.FileDeletedBeforeScan),
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent2 = Intent(
                                    this@AudioActivity.applicationContext,
                                    ScanImagesActivty::class.java
                                )
                                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                this@AudioActivity.startActivity(intent2)
                            }
                        }
                        adapter?.setAllImagesUnseleted()
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
        mRecoverPhotosAsyncTask?.execute()
    }

    fun deleteActions() {
        if (adapter?.selectedItem?.size == 0) {
            Toast.makeText(this, getString(R.string.can_not_process), Toast.LENGTH_LONG).show()
            return
        }
        binding?.checkboxSelectAll?.isChecked = false
        this.mRecoverPhotosAsyncTask = adapter?.selectedItem?.let {
            RecoverAudioAsyncTask(
                this,
                it, true, object : RecoverAudioAsyncTask.OnRestoreListener {
                    override fun onComplete(str: String?) {
                        val it = mList.iterator()
                        while (it.hasNext()) {
                            val next = it.next()
                            if (next.isCheck) {
                                it.remove()
                                ScanImagesActivty.mAlbumAudio[int_position].listPhoto!!.remove(next)
                            }
                        }
                        adapter?.setAllImagesUnseleted()
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
        mRecoverPhotosAsyncTask?.execute(*arrayOfNulls<String>(0))
    }

    fun SDCardCheck(): Boolean {
        val externalFilesDirs = ContextCompat.getExternalFilesDirs(this, null as String?)
        return if ((externalFilesDirs.size <= 1 || externalFilesDirs[0] == null || externalFilesDirs[1] == null)) false else true
    }

    fun fileSearch() {
        startActivityForResult(Intent("android.intent.action.OPEN_DOCUMENT_TREE"), 100)
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
}
