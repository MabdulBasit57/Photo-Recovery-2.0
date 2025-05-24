package com.recover.photo.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import java.io.File

class MediaScanner(context: Context?, private val destFile: File) : MediaScannerConnectionClient {
    private val mScannerConnection = MediaScannerConnection(context, this)

    init {
        mScannerConnection.connect()
    }

    override fun onMediaScannerConnected() {
        mScannerConnection.scanFile(destFile.absolutePath, null)
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        mScannerConnection.disconnect()
    }
}
