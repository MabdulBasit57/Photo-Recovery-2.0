package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.decentapps.supre.photorecovery.datarecovery.BuildConfig
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils
import com.decentapps.supre.photorecovery.datarecovery.utils.AppUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdView

class AboutActivity : AppCompatActivity() {
    private var isEnabled = false
    // private InterstitialAd mInterstitialAd;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupActionBar()
        toggle()
        try {
            findViewById<FrameLayout>(R.id.adContainer).removeAllViews()
            showTempelateNativeAd(this,BuildConfig.native_home,findViewById<FrameLayout>(R.id.adContainer))
        } catch (e: Exception) {
           e.printStackTrace()
        }
    }
    private fun toggle() {
        val toggleRoot = findViewById<RelativeLayout>(R.id.toggleRoot)
        val privacyButton = findViewById<LinearLayout>(R.id.privacy_button)
        val rateus = findViewById<LinearLayout>(R.id.rateus_button)
        privacyButton.setOnClickListener {
            AppUtils.privacypolicy(this)
        }
        rateus.setOnClickListener {
            AppUtils.rateApp(this)
        }
        val toggleThumb = findViewById<View>(R.id.toggleThumb)

        isEnabled = areNotificationsEnabled()

        updateToggleUI(toggleRoot, toggleThumb, isEnabled)

        toggleRoot.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!isEnabled) {
                    requestNotificationPermission()
                } else {
                    // Optionally, open settings to manually disable
                    openAppNotificationSettings()
                }
            } else {
                Toast.makeText(this, "Notifications can only be toggled manually below Android 13", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val titleToolbar = findViewById<TextView>(R.id.titleToolbar)
        val backToolbar = findViewById<ImageView>(R.id.backToolbar)
        backToolbar.setOnClickListener {
            onBackPressed()
        }
        titleToolbar.text="Settings"
    }

    fun showTempelateNativeAd(context: Context, adUnitId: String, container: FrameLayout) {
        if(AdUtils.nativeAdAppSetting!=null){
            AdUtils.nativeAdAppSetting?.let { nativeAd->
                container.visibility=View.VISIBLE
                val inflater = LayoutInflater.from(context)
                val adView = inflater.inflate(R.layout.medium_native, null) as NativeAdView
                // Set views
                adView.mediaView = adView.findViewById(R.id.ad_media)
                adView.headlineView = adView.findViewById(R.id.ad_headline)
                adView.bodyView = adView.findViewById(R.id.ad_body)
                adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                adView.iconView = adView.findViewById(R.id.ad_app_icon)

                // Bind content
                (adView.headlineView as TextView).text = nativeAd.headline
                (adView.bodyView as TextView).text = nativeAd.body
                (adView.callToActionView as Button).text = nativeAd.callToAction
                container.removeAllViews()
                val icon = nativeAd.icon
                if (icon != null) {
                    (adView.iconView as ImageView).setImageDrawable(icon.drawable)
                    adView.iconView?.visibility = View.VISIBLE
                } else {
                    adView.iconView?.visibility = View.GONE
                }
                adView.setNativeAd(nativeAd)
                container.addView(adView)
            }

        }
        else if(AdUtils.nativeAdAppSettingNormal!=null){
            container.visibility=View.VISIBLE
            AdUtils.nativeAdAppSettingNormal?.let { nativeAd->

                val inflater = LayoutInflater.from(context)
                val adView = inflater.inflate(R.layout.small_native, null) as NativeAdView
                // Set views
                adView.mediaView = adView.findViewById(R.id.ad_media)
                adView.headlineView = adView.findViewById(R.id.ad_headline)
                adView.bodyView = adView.findViewById(R.id.ad_body)
                adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                adView.iconView = adView.findViewById(R.id.ad_app_icon)

                // Bind content
                (adView.headlineView as TextView).text = nativeAd.headline
                (adView.bodyView as TextView).text = nativeAd.body
                (adView.callToActionView as Button).text = nativeAd.callToAction
                container.removeAllViews()
                val icon = nativeAd.icon
                if (icon != null) {
                    (adView.iconView as ImageView).setImageDrawable(icon.drawable)
                    adView.iconView?.visibility = View.VISIBLE
                } else {
                    adView.iconView?.visibility = View.GONE
                }
                adView.setNativeAd(nativeAd)
                container.addView(adView)
            }

        }
        else{
                val adLoader = AdLoader.Builder(context, adUnitId)
                    .forNativeAd { nativeAd ->
                        val inflater = LayoutInflater.from(context)
                        val adView = inflater.inflate(R.layout.medium_native, null) as NativeAdView
                        // Set views
                        container.visibility=View.VISIBLE
                        adView.mediaView = adView.findViewById(R.id.ad_media)
                        adView.headlineView = adView.findViewById(R.id.ad_headline)
                        adView.bodyView = adView.findViewById(R.id.ad_body)
                        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                        adView.iconView = adView.findViewById(R.id.ad_app_icon)

                        // Bind content
                        (adView.headlineView as TextView).text = nativeAd.headline
                        (adView.bodyView as TextView).text = nativeAd.body
                        (adView.callToActionView as Button).text = nativeAd.callToAction

                        val icon = nativeAd.icon
                        if (icon != null) {
                            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
                            adView.iconView?.visibility = View.VISIBLE
                        } else {
                            adView.iconView?.visibility = View.GONE
                        }
                        adView.setNativeAd(nativeAd)
                        container.removeAllViews()
                        container.addView(adView)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e("NativeAd", "Failed to load: ${error.message}")
                        }
                    })
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }

    private fun areNotificationsEnabled(): Boolean {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    private fun updateToggleUI(toggleRoot: RelativeLayout, toggleThumb: View, enabled: Boolean) {
        toggleRoot.setBackgroundResource(if (enabled) R.drawable.bg_toggle_on else R.drawable.bg_toggle_off)
        val params = toggleThumb.layoutParams as RelativeLayout.LayoutParams
        if (enabled) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_START)
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
            toggleThumb.layoutParams = params
        } else {
            params.removeRule(RelativeLayout.ALIGN_PARENT_END)
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
            toggleThumb.layoutParams = params
        }
        isEnabled = enabled
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && permissions.contains(android.Manifest.permission.POST_NOTIFICATIONS)) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            val toggleRoot = findViewById<RelativeLayout>(R.id.toggleRoot)
            val toggleThumb = findViewById<View>(R.id.toggleThumb)
            updateToggleUI(toggleRoot, toggleThumb, granted)
        }
    }

    private fun openAppNotificationSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }
}
