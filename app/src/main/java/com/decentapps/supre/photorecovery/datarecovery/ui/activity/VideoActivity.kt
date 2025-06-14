package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.adapter.VideoAdapter
import com.decentapps.supre.photorecovery.datarecovery.databinding.ActivityPhotosBinding
import com.decentapps.supre.photorecovery.datarecovery.pj.VideoModel
import com.decentapps.supre.photorecovery.datarecovery.tasks.RecoverVideoAsyncTask
import com.decentapps.supre.photorecovery.datarecovery.tasks.RecoverVideoWorker
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils
import com.decentapps.supre.photorecovery.datarecovery.utils.RecoverUtils
import java.io.File

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1123
                )
            }
            else{
                binding?.checkboxSelectAll?.isChecked = false
                adapter?.selectedItem?.let {
                    startRecovery(this,it,true,null)
               /*     RecoverVideoAsyncTask(
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
                        })*/
                }
            }
        }
        else{
            binding?.checkboxSelectAll?.isChecked = false
            adapter?.selectedItem?.let {
                startRecovery(this,it,true,null)
                /*     RecoverVideoAsyncTask(
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
                         })*/
            }
        }

     /*   binding?.checkboxSelectAll?.isChecked = false
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
        mRecoverVideoAsyncTask?.execute(*arrayOfNulls<String>(0))*/
    }
    private val recoveryCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            AdUtils.showHomeInterstitialAd(this@VideoActivity) {
                val count = intent?.getIntExtra("value", 0) ?: 0
                val type = intent?.getIntExtra("type", 1) ?: 1

                val goIntent = Intent(this@VideoActivity, RecoveredVideosActivity::class.java)
                goIntent.putExtra("value", count)
                goIntent.putExtra("type", type)
                startActivity(goIntent)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(recoveryCompleteReceiver, IntentFilter("recovery_completed"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(recoveryCompleteReceiver)
    }
    fun startRecovery(
        context: Context,
        videoList: ArrayList<VideoModel>,
        delete: Boolean = false,
        deleteDir: File? = null
    ) {
        val data = workDataOf(
            "video_list" to RecoverUtils.toJson(videoList),
            "delete" to delete,
            "delete_dir" to deleteDir?.absolutePath
        )

        val request = OneTimeWorkRequestBuilder<RecoverVideoWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }


    fun deleteActions() {
        if (adapter?.selectedItem?.size == 0) {
            Toast.makeText(this, getString(R.string.can_not_process), Toast.LENGTH_LONG).show()
            return
        }
        binding?.checkboxSelectAll?.isChecked = false
        adapter?.selectedItem?.let {
            startRecovery(this,it,true,null)
        /*    RecoverVideoAsyncTask(
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
                })*/
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

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(recoveryCompleteReceiver)
    }
}
