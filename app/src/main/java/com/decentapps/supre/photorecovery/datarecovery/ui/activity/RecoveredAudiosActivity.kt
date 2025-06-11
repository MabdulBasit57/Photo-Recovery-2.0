package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.ads.AdView
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.adapter.MainListener
import com.decentapps.supre.photorecovery.datarecovery.adapter.RecoveredAudiosAdapter
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import java.io.File

class RecoveredAudiosActivity : AppCompatActivity(), MainListener {
    var filesRecyclerView: RecyclerView? = null
    var relativeLayout: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovered_images)

        title = "Recovered Audios"
        relativeLayout = findViewById(R.id.deleteLay)

        filesRecyclerView = findViewById(R.id.filesRecyclerView)
        filesRecyclerView?.setHasFixedSize(true)
        filesRecyclerView?.setAdapter(RecoveredAudiosAdapter(this, allFiles, this))
    }

    val allFiles: List<File>
        get() {
            val files: MutableList<File> = ArrayList()
            val f = File(Utils.getPathSave(this, getString(R.string.restore_folder_path_audio)))

            val FileList = f.listFiles()

            if (FileList == null) {
                relativeLayout?.visibility = View.VISIBLE
         /*       val mAdView = findViewById<AdView>(R.id.madView)
                mAdView.visibility = View.GONE*/
                Toast.makeText(this, "No Audios Recovered Yet", Toast.LENGTH_SHORT).show()
            } else {
                relativeLayout = findViewById(R.id.deleteLay)
                relativeLayout?.visibility = View.GONE
                for (file in FileList) {
                    if (file.name.endsWith(".mp3") || file.name.endsWith(".opus")) {
                        files.add(file)
                    }
                }
             /*   if (!files.isEmpty()) {
                    val mAdView = findViewById<AdView>(R.id.madView)
                    Utils.showBannerAd(mAdView)
                }*/
            }
            return files
        }

    override fun begin(position: Int) {
        val a = FileProvider.getUriForFile(this, "$packageName.provider", allFiles[position])
        val viewMediaIntent = Intent()
        viewMediaIntent.setAction(Intent.ACTION_VIEW)
        viewMediaIntent.setDataAndType(a, "audio/*")
        viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(viewMediaIntent)
    }
}