package com.recover.photo.pj

class AlbumPhoto {
    @JvmField
    var lastModified: Long = 0
    @JvmField
    var listPhoto: ArrayList<PhotoModel>? = null
    @JvmField
    var str_folder: String? = null
}
