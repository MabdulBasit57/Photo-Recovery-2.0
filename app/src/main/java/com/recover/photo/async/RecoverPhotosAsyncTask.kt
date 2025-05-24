package com.recover.photo.async

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.recover.photo.pj.ImageData
import com.recover.photo.utils.Config
import com.recover.photo.utils.MediaScanner
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecoverPhotosAsyncTask(
    @field:SuppressLint("StaticFieldLeak") private val mContext: Context,
    private val alImageData: ArrayList<ImageData>,
    private val handler: Handler?
) : AsyncTask<String?, String?, String?>() {
    private var progressDialog: ProgressDialog? = null

    override fun onPreExecute() {
        super.onPreExecute()
        this.progressDialog = ProgressDialog(this.mContext)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Restoring")
        progressDialog!!.show()
    }

    override fun doInBackground(vararg strAr: String?): String? {
        for (strArr in alImageData.indices) {
            val sourceFile = File(alImageData[strArr].filePath)
            val fileDirectory = File(Config.IMAGE_RECOVER_DIRECTORY)
            val stringBuilder = Config.IMAGE_RECOVER_DIRECTORY +
                    File.separator +
                    getFileName(strArr)
            val destinationFile = File(stringBuilder)
            try {
                if (!destinationFile.exists()) {
                    fileDirectory.mkdirs()
                }
                copy(sourceFile, destinationFile)
                val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                intent.setData(Uri.fromFile(destinationFile))
                mContext.sendBroadcast(intent)
                MediaScanner(this.mContext, destinationFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun copy(file: File?, file2: File?) {
        val source = FileInputStream(file).channel
        val destination = FileOutputStream(file2).channel

        source.transferTo(0, source.size(), destination)
        source.close()
        destination?.close()
    }


    fun getFileName(i: Int): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US)
        val stringBuilder = simpleDateFormat.format(date) +
                i +
                ".png"
        return stringBuilder
    }

    override fun onPostExecute(str: String?) {
        Toast.makeText(mContext, "Restored successfully", Toast.LENGTH_SHORT).show()
        if (this.progressDialog != null) {
            progressDialog!!.cancel()
            this.progressDialog = null
        }
        if (this.handler != null) {
            val obtain = Message.obtain()
            obtain.what = Config.REPAIR
            obtain.obj = str
            handler.sendMessage(obtain)
        }
        super.onPostExecute(str)
    }
}
