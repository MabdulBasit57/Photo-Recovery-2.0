package com.recover.photo.ui.activity

import android.animation.ObjectAnimator
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.recover.photo.R
import com.recover.photo.utils.AppUtils

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class AboutActivity : AppCompatActivity() {
    private var isEnabled = false
    // private InterstitialAd mInterstitialAd;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupActionBar()
        toggle()
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
