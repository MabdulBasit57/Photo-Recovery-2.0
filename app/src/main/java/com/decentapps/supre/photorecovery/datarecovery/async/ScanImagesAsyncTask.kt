package com.decentapps.supre.photorecovery.datarecovery.async

import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.os.Message
import com.decentapps.supre.photorecovery.datarecovery.pj.ImageData
import com.decentapps.supre.photorecovery.datarecovery.utils.Config
import java.io.File

class ScanImagesAsyncTask(private val context: Context, private val handler: Handler?) :
    AsyncTask<String?, Int?, ArrayList<ImageData?>>() {
    private val alImageData: ArrayList<ImageData?> = ArrayList<ImageData?>()
    private var progressDialog: ProgressDialog? = null
    var i: Int = 0

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strAr: String?): ArrayList<ImageData?> {
        val strArr = if (strAr[0].equals("all", ignoreCase = true)) {
            //if all then scan for complete internal and external storage
            Environment.getExternalStorageDirectory().absolutePath
        } else {
            //otherwise only scan items in "RestoredPics" folder (app folder).
            Environment.getExternalStorageDirectory().absolutePath + "/RestoredPhotos"
        }

        val stringBuilder = StringBuilder()
        stringBuilder.append("root = ")
        stringBuilder.append(strArr)
        checkFileOfDirectory(getFileList(strArr))
        return this.alImageData
    }

    fun checkFileOfDirectory(fileArr: Array<File>?) {
        if (fileArr != null) {
            for (file in fileArr) {
                val numArr = arrayOfNulls<Int>(1)
                val i2 = this.i
                this.i = i2 + 1
                numArr[0] = i2
                publishProgress(*numArr)
                if (file.isDirectory) {
                    checkFileOfDirectory(getFileList(file.path))
                } else {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(file.path, options)
                    if (!(options.outWidth == -1 || options.outHeight == -1)) {
                        if (file.path.endsWith(".exo") || file.path.endsWith(".mp3") || file.path.endsWith(
                                ".mp4"
                            )
                            || file.path.endsWith(".pdf") || file.path.endsWith(".apk") || file.path.endsWith(
                                ".txt"
                            )
                            || file.path.endsWith(".doc") || file.path.endsWith(".exi") || file.path.endsWith(
                                ".dat"
                            )
                            || file.path.endsWith(".m4a") || file.path.endsWith(".json") || file.path.endsWith(
                                ".chck"
                            )
                        ) {
                            //do nothing, just skip these files
                        } else {
                            alImageData.add(ImageData(file.path, false))
                        }
                    }
                }
            }
        }
    }

    fun getFileList(str: String?): Array<File>? {
        val file = File(str)
        if (!file.isDirectory) {
            return null
        }
        return file.listFiles()
    }

    override fun onProgressUpdate(vararg numArr: Int?) {
        if (this.handler != null) {
            val message = Message.obtain()
            message.what = Config.UPDATE
            message.obj = numArr[0]
            handler.sendMessage(message)
        }
    }

    override fun onPostExecute(arrayList: ArrayList<ImageData?>) {
        if (this.progressDialog != null) {
            progressDialog!!.cancel()
            this.progressDialog = null
        }
        if (this.handler != null) {
            val message = Message.obtain()
            message.what = Config.DATA
            message.obj = arrayList
            handler.sendMessage(message)
        }
        super.onPostExecute(arrayList)
    }
}
