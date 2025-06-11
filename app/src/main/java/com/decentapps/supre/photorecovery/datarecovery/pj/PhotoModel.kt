package com.decentapps.supre.photorecovery.datarecovery.pj

class PhotoModel(@JvmField var pathPhoto: String, var lastModified: Long, @JvmField var sizePhoto: Long) {
    @JvmField
    var isCheck: Boolean = false
}

