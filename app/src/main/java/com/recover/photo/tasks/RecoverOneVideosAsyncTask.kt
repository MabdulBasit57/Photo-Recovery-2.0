package com.recover.photo.tasks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.recover.photo.R
import com.recover.photo.pj.VideoModel
import com.recover.photo.utils.MediaScanner
import com.recover.photo.utils.Utils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class RecoverOneVideosAsyncTask(
    private val mContext: Context,
    private val mVideo: VideoModel,
    private val onRestoreListener: OnRestoreListener?
) : AsyncTask<String?, Int?, String?>() {

    var dialog: AlertDialog? = null

    var tvNumber: TextView? = null


    fun Show_Diloge(con: Context) {
        val alertCustomdialog = LayoutInflater.from(con).inflate(R.layout.cd, null)
        val alert = AlertDialog.Builder(
            con
        )
        tvNumber = alertCustomdialog.findViewById(R.id.tvNumber)
        alert.setView(alertCustomdialog)
        dialog = alert.create()
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    interface OnRestoreListener {
        fun onComplete(str: String?)
    }


    public override fun onPreExecute() {
        super.onPreExecute()
        if (!(mContext as Activity).isFinishing) {
            Show_Diloge(this.mContext)
        }
    }


    override fun doInBackground(vararg strArr: String?): String? {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }


        Log.e("doInBack", "1")
        val file = File(mVideo.pathPhoto)
        val context = this.mContext
        val file2 =
            File(Utils.getPathSave(context, context.getString(R.string.restore_folder_path_video)))
        val sb = StringBuilder()
        val context2 = this.mContext
        sb.append(
            Utils.getPathSave(
                context2,
                context2.getString(R.string.restore_folder_path_video)
            )
        )
        sb.append(File.separator)
        Log.e("doInBack", "2")

        if (!file.exists()) {
            return "Er1"
        }
        sb.append(getFileName(mVideo.pathPhoto))
        val file3 = File(sb.toString())
        try {
            if (!file3.exists()) {
                file2.mkdirs()
            }
            copy(file, file3)
            if (Build.VERSION.SDK_INT >= 19) {
                val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                intent.setData(Uri.fromFile(file3))
                mContext.sendBroadcast(intent)
            }
            MediaScanner(this.mContext, file3)
        } catch (e2: IOException) {
            e2.printStackTrace()
            Log.e("doInBack", "e2" + e2.message)
        }

        Log.e("doInBack", "3")

        try {
            Thread.sleep(2000)
            return null
        } catch (e3: InterruptedException) {
            e3.printStackTrace()
            return null
        }
    }

    @Throws(IOException::class)
    fun copy(file: File?, file2: File?) {
        val channel = FileInputStream(file).channel
        val channel2 = FileOutputStream(file2).channel
        channel!!.transferTo(0, channel.size(), channel2)
        if (channel != null) {
            channel.close()
        }
        channel2?.close()
    }

    fun getFileName(str: String): String {
        val substring = str.substring(str.lastIndexOf("/") + 1)
        if (substring.endsWith(".3gp") || substring.endsWith(".mp4") || substring.endsWith(".mkv") || substring.endsWith(
                ".flv"
            )
        ) {
            return substring
        }
        return "$substring.mp4"
    }


    public override fun onPostExecute(str: String?) {
        var str = str
        super.onPostExecute(str)
        try {
            if (this.dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
                this.dialog = null
            }
        } catch (unused: Exception) {
        }
        if (this.onRestoreListener != null) {
            if (str == null) {
                str = ""
            }
            onRestoreListener.onComplete(str)
        }
    }


    override fun onProgressUpdate(vararg numArr: Int?) {
        super.onProgressUpdate(*numArr)
        tvNumber?.text = mContext.getString(R.string.restoring_video)
    }
}
