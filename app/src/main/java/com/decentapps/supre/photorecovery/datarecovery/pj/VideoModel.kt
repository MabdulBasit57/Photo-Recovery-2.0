package com.decentapps.supre.photorecovery.datarecovery.pj

import java.io.Serializable

class VideoModel(
    @JvmField var pathPhoto: String,
    var lastModified: Long,
    @JvmField var sizePhoto: Long,
    @JvmField var typeFile: String,
    @JvmField var timeDuration: String
) : Serializable {
    @JvmField
    var isCheck: Boolean = false
}

