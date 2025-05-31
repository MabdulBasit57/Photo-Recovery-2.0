package com.recover.photo.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.util.Log
import java.io.File

class MediaScanner(context: Context?, private val destFile: File) : MediaScannerConnectionClient {

    private var scannerConnection: MediaScannerConnection? = null

    init {
        if (context != null && destFile.exists()) {
            scannerConnection = MediaScannerConnection(context, this).apply {
                connect()
            }
        } else {
            Log.w("MediaScanner", "Invalid context or file does not exist: ${destFile.path}")
        }
    }

    override fun onMediaScannerConnected() {
        scannerConnection?.scanFile(destFile.absolutePath, null)
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        try {
            scannerConnection?.disconnect()
        } catch (e: Exception) {
            Log.e("MediaScanner", "Error disconnecting MediaScannerConnection", e)
        }

        if (uri == null) {
            Log.w("MediaScanner", "Scan completed with null URI for file: $path")
        }
    }
}

