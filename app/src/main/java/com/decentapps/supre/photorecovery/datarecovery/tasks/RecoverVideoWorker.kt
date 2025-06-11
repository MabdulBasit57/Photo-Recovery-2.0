package com.decentapps.supre.photorecovery.datarecovery.tasks

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.ui.activity.RecoveredFilesActivity
import com.decentapps.supre.photorecovery.datarecovery.utils.MediaScanner
import com.decentapps.supre.photorecovery.datarecovery.utils.RecoverUtils
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class RecoverVideoWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val videoListJson = inputData.getString("video_list") ?: return Result.failure()
        val delete = inputData.getBoolean("delete", false)

        val videoList = RecoverUtils.fromJson(videoListJson)
        val dir = File(inputData.getString("delete_dir") ?: "")

        try {
            setForeground(createForegroundInfo(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (delete) {
            for (i in videoList.indices) {
                val file = File(videoList[i].pathPhoto)
                if (file.exists()) {
                    var i2 = 0
                    if (dir?.exists() == true) {
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
                    try {
                        val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                        intent.setData(Uri.fromFile(file))
                        context?.sendBroadcast(intent)
                        MediaScanner(context, file)
                    } catch (e: Exception) {
                       e.printStackTrace()
                    }

//                    var count = i + 1
//                    publishProgress(count)
                    try {
                        setForeground(createForegroundInfo(i + 1))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    delay(1000L)
                }
            }
        }
        for (i3 in videoList.indices) {
            val file2 = File(videoList[i3].pathPhoto)
            val context = context
            val file3 = File(
                Utils.getPathSave(
                    context,
                    context.getString(R.string.restore_folder_path_video)
                )
            )
            val sb = StringBuilder()
            val context2 = context
            sb.append(
                Utils.getPathSave(
                    context2,
                    context2.getString(R.string.restore_folder_path_video)
                )
            )
            sb.append(File.separator)
            if (!file2.exists()) {
//                copyFile(file3, file2)
//                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file3)))
            }
            sb.append(getFileName(videoList[i3].pathPhoto))
            val file4 = File(sb.toString())
            if (!file4.exists()) {
                file3.mkdirs()
            }
            try {
                copy(file2, file4)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                val intent2 = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                intent2.setData(Uri.fromFile(file4))
                context.sendBroadcast(intent2)
                MediaScanner(context, file4)
            } catch (e: Exception) {
               e.printStackTrace()
            }
            /* try {
                 setForeground(createForegroundInfo(i3 + 1))
             } catch (e: Exception) {
                e.printStackTrace()
             }*/
            delay(1000L)
        }
       /* for ((index, video) in videoList.withIndex()) {
            val source = File(video.pathPhoto)
            if (!source.exists()) {
                Log.w("Recover", "Source file missing: ${video.pathPhoto}")
                continue
            }


            if (delete) {
                // Wipe and delete file
                if (source.canWrite()) {
                    try {
                        repeat(2) {
                            FileOutputStream(source, false).use { it.write("0".toByteArray()) }
                        }
                    } catch (e: Exception) {
                        Log.e("Recover", "Wipe failed: ${e.message}")
                    }
                }
                source.delete()
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(source)))
            } else {
                val restoreDir = File(RecoverUtils.getPathSave( context.getString(R.string.restore_folder_path_video)))
                if (!restoreDir.exists()) restoreDir.mkdirs()

                val dest = File(restoreDir, getFileName(video.pathPhoto))
                if (!dest.exists()) {
                    copyFile(source, dest)
                    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dest)))
                }
            }

            setForeground(createForegroundInfo(index + 1))
            delay(1000L)
        }*/

        // ✅ Send Broadcast when done
        val intent = Intent("recovery_completed")
        intent.putExtra("value", videoList.size)
        intent.putExtra("type", if (delete) 2 else 1)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        return Result.success()
    }

    @Throws(IOException::class)
    fun copy(file: File?, file2: File?) {
        val channel = FileInputStream(file).channel
        val channel2 = FileOutputStream(file2).channel
        channel?.transferTo(0, channel.size(), channel2)
        if (channel != null) {
            channel.close()
        }
        channel2?.close()
    }
    private fun getFileName(path: String): String {
        val name = path.substringAfterLast("/")
        return if (name.endsWith(".mp4") || name.endsWith(".3gp") || name.endsWith(".mkv") || name.endsWith(".flv"))
            name
        else "$name.mp4"
    }

    private fun copyFile(src: File, dest: File) {
        try {
            FileInputStream(src).channel.use { sourceChannel ->
                FileOutputStream(dest).channel.use { destChannel ->
                    sourceChannel.transferTo(0, sourceChannel.size(), destChannel)
                }
            }
            Log.d("Recover", "Copied: ${src.absolutePath} -> ${dest.absolutePath}")
        } catch (e: Exception) {
            Log.e("Recover", "Copy failed from ${src.path} to ${dest.path}: ${e.message}")
        }
    }
    fun createForegroundInfo(progress: Int): ForegroundInfo {
        val channelId = "recover_worker_channel"
        val notificationId = 1
        val context = applicationContext

        // ✅ Create Notification Channel (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Recovery Worker Channel"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification for video recovery"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // ✅ Cancel intent for the worker
        val cancelIntent = WorkManager.getInstance(context).createCancelPendingIntent(id)

        // ✅ Build notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Recovering Data")
            .setContentText("Recovering $progress item(s)")
            .setSmallIcon(R.drawable.appicon) // Make sure this exists
            .addAction(android.R.drawable.ic_delete, "Cancel", cancelIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(true)
            .setContentIntent(makePendingIntent(context))
            .build()

        // ✅ ForegroundInfo with proper type (Android 14+ requires this)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private fun makePendingIntent(ctx: Context): PendingIntent {
        val splashIntent = Intent(ctx, RecoveredFilesActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = "com.decentapps.supre.photorecovery.datarecovery.ACTION_REBOOT_FROM_NOTIF"
            putExtra("internalnotification", "internalnotification")
        }

        val pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE or
                PendingIntent.FLAG_ONE_SHOT

        return PendingIntent.getActivity(ctx, 0, splashIntent, pendingFlags)
    }



}
