package com.recover.photo.utils

import android.os.Environment
import java.io.File

object Config {
    const val DATA: Int = 1000
    const val REPAIR: Int = 2000
    const val UPDATE: Int = 3000
    val IMAGE_RECOVER_DIRECTORY: String

    init {
        val sbDirectory = StringBuilder()
        sbDirectory.append(Environment.getExternalStorageDirectory())
        sbDirectory.append(File.separator)
        sbDirectory.append("RestoredPhotos")
        IMAGE_RECOVER_DIRECTORY = sbDirectory.toString()
    }
}
