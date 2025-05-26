package com.recover.photo.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.recover.photo.R
import com.recover.photo.adapter.VideoAdapter
import com.recover.photo.databinding.ActivityPhotosBinding
import com.recover.photo.pj.VideoModel
import com.recover.photo.tasks.RecoverVideoAsyncTask

class VideoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPhotosBinding.inflate(layoutInflater) }
    var adapter: VideoAdapter? = null
    var int_position: Int = 0
    var mList: ArrayList<VideoModel> = ArrayList()
    var mRecoverVideoAsyncTask: RecoverVideoAsyncTask? = null

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(binding.root)
        intData()
        binding.customToolbar.backToolbar.setOnClickListener {
            onBackPressed()
        }
        binding.customToolbar.titleToolbar.text="Video Recovery"
    }

    fun intData() {
        this.int_position = intent.getIntExtra("value", 0)
        if (ScanImagesActivty.mAlbumVideo != null && ScanImagesActivty.mAlbumVideo.size > this.int_position) {
            mList.addAll(ScanImagesActivty.mAlbumVideo?.get(int_position)?.listPhoto?.clone() as ArrayList<VideoModel>)
        }
        this.adapter = VideoAdapter(this, this.mList)
        binding?.gvFolder?.adapter = this.adapter
        binding?.apply {
            checkboxSelectAll?.setOnCheckedChangeListener { compoundButton: CompoundButton?, z: Boolean ->
                if (!z) {
                    adapter?.setAllImagesUnseleted()
                } else {
                    adapter?.selectAll()
                }
            }
            btnDelete?.setOnClickListener { view: View? ->
                val builder = AlertDialog.Builder(this@VideoActivity)
                builder.setTitle(this@VideoActivity.getString(R.string.delete_title) as CharSequence)
                builder.setMessage(this@VideoActivity.getString(R.string.delete_bdy) as CharSequence)
                builder.setPositiveButton("Ok") { dialogInterface: DialogInterface?, i: Int -> this@VideoActivity.deleteActions() }
                builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface?, i: Int -> }
                val create = builder.create()
                if (!this@VideoActivity.isFinishing) {
                    create.show()
                }
            }
            btnRestore?.setOnClickListener { view: View? ->
                val selectedItem =
                    adapter?.selectedItem
                if (selectedItem?.size == 0) {
                    Toast.makeText(
                        this@VideoActivity,
                        getString(R.string.can_not_process),
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

    fun SResIT(arrayList: ArrayList<*>) {
        binding?.checkboxSelectAll?.isChecked = false
        this.mRecoverVideoAsyncTask = adapter?.selectedItem?.let {
            RecoverVideoAsyncTask(
                this,
                it, object : RecoverVideoAsyncTask.OnRestoreListener {
                    override fun onComplete(str: String?) {
                        if (str?.isEmpty() == true) {
                            val intent = Intent(this@VideoActivity, RecoveredVideosActivity::class.java)
                            intent.putExtra("value", arrayList.size)
                            intent.putExtra("type", 1)
                            this@VideoActivity.startActivity(intent)
                        } else if (str == "Er1") {
                            val videoActivity = this@VideoActivity
                            Toast.makeText(
                                videoActivity,
                                videoActivity.getString(R.string.FileDeletedBeforeScan),
                                Toast.LENGTH_LONG
                            ).show()
                            val intent2 = Intent(
                                this@VideoActivity.applicationContext,
                                ScanImagesActivty::class.java
                            )
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            this@VideoActivity.startActivity(intent2)
                        }
                        adapter?.setAllImagesUnseleted()
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
        mRecoverVideoAsyncTask!!.execute(*arrayOfNulls<String>(0))
    }


    fun deleteActions() {
        if (adapter?.selectedItem?.size == 0) {
            Toast.makeText(this, getString(R.string.can_not_process), Toast.LENGTH_LONG).show()
            return
        }
        binding?.checkboxSelectAll?.isChecked = false
        this.mRecoverVideoAsyncTask = adapter?.selectedItem?.let {
            RecoverVideoAsyncTask(
                this,
                it, true, object : RecoverVideoAsyncTask.OnRestoreListener {
                    override fun onComplete(str: String?) {
                        val it = mList.iterator()
                        while (it.hasNext()) {
                            val next = it.next()
                            if (next.isCheck) {
                                it.remove()
                                ScanImagesActivty.mAlbumVideo[int_position].listPhoto!!.remove(next)
                            }
                        }
                        adapter?.setAllImagesUnseleted()
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
        mRecoverVideoAsyncTask?.execute()
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
