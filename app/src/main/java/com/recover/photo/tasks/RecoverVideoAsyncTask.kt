package com.recover.photo.tasks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.recover.photo.R
import com.recover.photo.pj.VideoModel
import com.recover.photo.utils.MediaScanner
import com.recover.photo.utils.Utils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class RecoverVideoAsyncTask : AsyncTask<String?, Int?, String?> {
    private val TAG: String = javaClass.name
    var count: Int = 0
    var delete: Boolean = false
    private var listPhoto: ArrayList<VideoModel>
    private var mContext: Context
    private var onRestoreListener: OnRestoreListener?
    var dir: File? = null

    var tvNumber: TextView? = null
    var dialog: AlertDialog? = null

    fun Show_Diloge(con: Context?) {
        val alertCustomdialog = LayoutInflater.from(con).inflate(R.layout.cd, null)
        val alert = AlertDialog.Builder(
            con!!
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

    constructor(
        context: Context,
        arrayList: ArrayList<VideoModel>,
        onRestoreListener2: OnRestoreListener?
    ) {
        this.mContext = context
        this.listPhoto = arrayList
        this.onRestoreListener = onRestoreListener2
    }

    constructor(
        context: Context,
        arrayList: ArrayList<VideoModel>,
        z: Boolean,
        onRestoreListener2: OnRestoreListener?
    ) {
        this.mContext = context
        this.listPhoto = arrayList
        this.onRestoreListener = onRestoreListener2
        this.delete = z
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
        if (this.delete) {
            for (i in listPhoto.indices) {
                val file = File(listPhoto[i].pathPhoto)
                if (file.exists()) {
                    var i2 = 0
                    if (dir!!.exists()) {
                        while (i2 < 2) {
                            try {
                                val fileOutputStream = FileOutputStream(file, false)
                                fileOutputStream.write("0".toByteArray())
                                fileOutputStream.flush()
                                fileOutputStream.close()
                                i2++
                            } catch (unused: Exception) {
                            }
                        }
                        file.delete()
                    }
                    val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                    intent.setData(Uri.fromFile(file))
                    mContext.sendBroadcast(intent)
                    MediaScanner(this.mContext, file)
                    this.count = i + 1
                    publishProgress(this.count)
                }
            }
            return null
        }
        for (i3 in listPhoto.indices) {
            val file2 = File(listPhoto[i3].pathPhoto)
            val context = this.mContext
            val file3 = File(
                Utils.getPathSave(
                    context,
                    context.getString(R.string.restore_folder_path_video)
                )
            )
            val sb = StringBuilder()
            val context2 = this.mContext
            sb.append(
                Utils.getPathSave(
                    context2,
                    context2.getString(R.string.restore_folder_path_video)
                )
            )
            sb.append(File.separator)
            if (!file2.exists()) {
                return "Er1"
            }
            sb.append(getFileName(listPhoto[i3].pathPhoto))
            val file4 = File(sb.toString())
            if (!file4.exists()) {
                file3.mkdirs()
            }
            try {
                copy(file2, file4)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val intent2 = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
            intent2.setData(Uri.fromFile(file4))
            mContext.sendBroadcast(intent2)
            MediaScanner(this.mContext, file4)
            this.count = i3 + 1
            publishProgress(this.count)
            try {
                Thread.sleep(2000)
            } catch (e3: InterruptedException) {
                e3.printStackTrace()
            }
        }
        return null
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
        } catch (ignored: Exception) {
        }
        if (this.onRestoreListener != null) {
            if (str == null) {
                str = ""
            }
            onRestoreListener!!.onComplete(str)
        }
    }

    override fun onProgressUpdate(vararg numArr: Int?) {
        super.onProgressUpdate(*numArr)

        if (!this.delete) {
            tvNumber?.text =
                String.format(
                    mContext.getString(R.string.restoring_number_format),
                    numArr[0]
                )
            return
        }
        tvNumber?.text =
            String.format(
                mContext.getString(R.string.deleted_number_format),
                numArr[0]
            )
    }

    private fun storingDataDirectory(fileName: File) {
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + "Photos Recovered"
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + "Photos Recovered")
        }
        if (!dir!!.exists()) {
            val success = dir!!.mkdirs()
            if (!success) {
                Toast.makeText(mContext, "failed to create folder", Toast.LENGTH_SHORT).show()
                dir = null
            }
        }

        try {
            val f = File(dir!!.absolutePath + "/" + fileName.name)
            if (f.exists()) {
                f.delete()
            }
            f.createNewFile()

            val out = FileOutputStream(f)
            out.flush()
            out.close()
            Log.d("fPath", "storingDataDirectory: " + f.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fferror", "storingDataDirectory: " + e.message)
        }
        val intent2 = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
        intent2.setData(Uri.fromFile(fileName))
        mContext.sendBroadcast(intent2)
        MediaScanner(this.mContext, fileName)
    }
}