package com.recover.photo.utils

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.recover.photo.pj.VideoModel
import java.io.File

object RecoverUtils {
    private val gson = Gson()

    fun toJson(videoList: List<VideoModel>): String {
        return gson.toJson(videoList)
    }

    fun fromJson(json: String): ArrayList<VideoModel> {
        val type = object : TypeToken<ArrayList<VideoModel>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getPathSave(folder: String): String {
        val path = File(Environment.getExternalStorageDirectory(), folder)
        if (!path.exists()) path.mkdirs()
        return path.absolutePath
    }
}