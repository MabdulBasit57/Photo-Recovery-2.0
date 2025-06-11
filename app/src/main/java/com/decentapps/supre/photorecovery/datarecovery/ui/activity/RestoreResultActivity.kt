package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils

class RestoreResultActivity : AppCompatActivity() {
    var mName: String = ""
    var path: String = ""

    var type: Int = 0

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_restore_result)
        intView()
        intData()
    }

    fun intView() {
        this.type = intent.getIntExtra("type", 0)
        if (this.type == 0) {
            this.mName = getString(R.string.photo_recovery)
            this.path = Utils.getPathSave(
                this@RestoreResultActivity,
                getString(R.string.restore_folder_path_photo)
            )
        }
        if (this.type == 1) {
            this.mName = getString(R.string.video_recovery)
            this.path = Utils.getPathSave(
                this@RestoreResultActivity,
                getString(R.string.restore_folder_path_video)
            )
        }
        if (this.type == 2) {
            this.mName = getString(R.string.audio_recovery)
            this.path = Utils.getPathSave(
                this@RestoreResultActivity,
                getString(R.string.restore_folder_path_audio)
            )
        }
        title = mName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun intData() {
        (findViewById<View>(R.id.tvStatus) as TextView).text =
            intent.getIntExtra("value", 0).toString()
        (findViewById<View>(R.id.tvPath) as TextView).text = """
    File Restored to
    /${path}
    """.trimIndent()
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
