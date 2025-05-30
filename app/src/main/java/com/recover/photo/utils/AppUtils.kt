package com.recover.photo.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object AppUtils {
    var filter:Int=0
     fun rateApp(context:Context) {
        try {
            val rateIntent = rateIntentForUrl("market://details?id=",context)
            context.startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details?id=",context)
            context.startActivity(rateIntent)
        }
    }
    private fun rateIntentForUrl(url: String,context: Context): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url + context.packageName))
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags =
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        intent.addFlags(flags)
        return intent
    }
    fun privacypolicy(activity: Activity){
        try {
            val url = "https://docs.google.com/document/d/e/2PACX-1vSVLzGF6ORR0F_ZeTQpHcNX9wUh6buEsxCnu0H43cpIB96myJKWhhuvb1Opg_1WRjBTPhx3PjY30mqG/pub"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            activity.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
     fun moreApps(context: Context) {
        val developerId = "VenTech+Apps" // Replace with your developer ID
        val playStoreUri = "https://play.google.com/store/apps/developer?id=$developerId"

        try {
            // Try to open the Play Store app
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUri))
            intent.setPackage("com.android.vending") // Ensure it opens in the Play Store app
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Play Store app is not installed, open the link in a browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUri))
            context.startActivity(intent)
        }
    }
     fun shareApp(context: Context) {
         try {
             val appPackageName = context.packageName // Get the app's package name
             val playStoreLink = "https://play.google.com/store/apps/details?id=$appPackageName"

             // Create the share intent
             val shareIntent = Intent(Intent.ACTION_SEND).apply {
                 type = "text/plain"
                 putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
                 putExtra(Intent.EXTRA_TEXT, "I found this awesome app. You should try it out!\n$playStoreLink")
             }

             // Launch the share dialog
             context.startActivity(Intent.createChooser(shareIntent, "Share via"))
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }
}