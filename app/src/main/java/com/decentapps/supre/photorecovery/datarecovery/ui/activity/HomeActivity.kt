package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.decentapps.supre.photorecovery.datarecovery.BuildConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.databinding.ActivityHomeBinding
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils.requestHomeInterstitialAd
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import com.decentapps.supre.photorecovery.datarecovery.utils.percentage
import com.decentapps.supre.photorecovery.datarecovery.utils.storageValue
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class HomeActivity :AppCompatActivity(){
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getStorage()
        checkPermissions()
        clickListener()
        AdUtils.loadNative(this,BuildConfig.hf_native_home)
        AdUtils.loadNativeSetting(this,BuildConfig.hf_native_setting)
        try {
            findViewById<FrameLayout>(R.id.adContainer).removeAllViews()
            binding?.adContainer?.let { showTempelateNativeAd(this,BuildConfig.native_home, it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        requestHomeInterstitialAd(this,BuildConfig.home_interstitial)
        binding.customToolbar.backToolbar.setOnClickListener {
            onBackPressed()
        }
        binding.customToolbar.setting.visibility=View.VISIBLE
        binding.customToolbar.setting.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.customToolbar.titleToolbar.text="Recovery App"
        binding.customToolbar.backToolbar.visibility=View.GONE
        // Initialize views
    }
    fun showTempelateNativeAd(context: Context, adUnitId: String, container: FrameLayout) {
        if(AdUtils.nativeAdApp!=null){
            container.visibility=View.VISIBLE
            AdUtils.nativeAdApp?.let { nativeAd->

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
        else if(AdUtils.nativeAdAppNormal!=null){
            container.visibility=View.VISIBLE
            AdUtils.nativeAdAppNormal?.let { nativeAd->

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
                    val adView = inflater.inflate(R.layout.small_native, null) as NativeAdView
                    container.visibility=View.VISIBLE
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
    override fun onBackPressed() {
        showRecoverDialog("Do you want to close the app?")
//        super.onBackPressed()
    }
    fun showRecoverDialog(msg:String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.exit_dialog)
        dialog.setCancelable(true)
        val btnYes = dialog.findViewById<TextView>(R.id.btnYes)
        val icon = dialog.findViewById<ImageView>(R.id.imgPhoto)
        Glide.with(this)
            .load(R.drawable.exit_svg)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    icon.background = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        val message = dialog.findViewById<TextView>(R.id.tvMessage)
        message.text=msg
        val btnNo = dialog.findViewById<TextView>(R.id.btnNo)

        btnYes.setOnClickListener {
            finish()
            dialog.dismiss()
        }
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    private fun clickListener() {
        binding?.apply {
            recoverDeletedData?.setOnClickListener {
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            }
            recoveredData?.setOnClickListener {
                checkButtonsPermissions(RecoveredFilesActivity::class.java)
            }
        }
    }

    private fun updateStorageUI(freePercentage: Int, usedSpace: String, total: String) {
        when {
            freePercentage > 80 -> {  try {
                Glide.with(this)
                    .load(R.drawable.red_progress)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            binding.percentageBG.background = resource
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }}
            freePercentage in 51..80 -> {
                try {
                    Glide.with(this)
                        .load(R.drawable.orange_progress)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                binding.percentageBG.background = resource
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }}
            freePercentage in 36..50 -> {
                try {
                    Glide.with(this)
                        .load(R.drawable.blue_progress)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                binding.percentageBG.background = resource
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }}
            else -> {
                try {
                    Glide.with(this)
                        .load(R.drawable.green_progress)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                binding.percentageBG.background = resource
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
                } catch (e: Exception) {
                    e.printStackTrace()
                }}
        }
        binding.percentagefree.text = "$freePercentage% Used"
        binding.percentagefree.visibility = View.VISIBLE
        binding.totalTitle.text = usedSpace+" of "+total
        storageValue =usedSpace+" of "+total
        percentage = "$freePercentage %"
    }
    private fun getStorage() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val stat = StatFs(Environment.getExternalStorageDirectory().path)
                val bytesAvailable = stat.blockSizeLong * stat.blockCountLong
                val bytesFree = stat.blockSizeLong * stat.availableBlocksLong
                val bytesUsed = bytesAvailable - bytesFree

                val units = arrayOf("B", "KB", "MB", "GB", "TB")

                val total = formatSize(bytesAvailable, units)
                val available = formatSize(bytesFree, units)
                val usedSpace = formatSize(bytesUsed, units)

                val usedPercentage =
                    (bytesUsed.toDouble() / bytesAvailable.toDouble() * 100).toInt()
                val freePercentage = 100 - usedPercentage

                withContext(Dispatchers.Main) {
                    // Update UI based on storage usage
                    updateStorageUI(usedPercentage, usedSpace, total)
                }
            } catch (e: Exception) {
                Log.e("StorageError", "Error calculating storage: ${e.message}")
            }
        }
    }

    private fun formatSize(size: Long, units: Array<String>): String {
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
    private fun checkButtonsPermissions(pos: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isReadMediaVideoPermissionGranted &&
                isReadMediaAudioPermissionGranted && isReadMediaImagesPermissionGranted
            ) {
                val intent = Intent(this, ScanImagesActivty::class.java)
                intent.putExtra("position", pos)
                startActivity(intent)
            } else {
                showPermissionDeniedToast()
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            if (isReadExternalPermissionGranted) {
                val intent = Intent(this, ScanImagesActivty::class.java)
                intent.putExtra("position", pos)
                startActivity(intent)
            } else {
                showPermissionDeniedToast()
            }
        } else {
            if (isWriteExternalPermissionGranted && isReadExternalPermissionGranted) {
                val intent = Intent(this, ScanImagesActivty::class.java)
                intent.putExtra("position", pos)
                startActivity(intent)
            } else {
                showPermissionDeniedToast()
            }
        }
    }
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this@HomeActivity)
                .withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_CONTACTS
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            forOpenAd()
                            // All permissions granted, proceed with your logic
                        } else {
                            // Check if any permission is permanently denied
                            if (report.isAnyPermissionPermanentlyDenied) {
                                // Permission is permanently denied, show explanation and request again
//                                    showPermissionDeniedToast();
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        // Show an explanation to the user
                        // You can display a dialog or message explaining why the permission is needed
                        // After showing the explanation, request the permission again
                        Utils.isAdAlreadyOpen = true
                        token.continuePermissionRequest()
                    }
                })
                .withErrorListener {
                    Toast.makeText(
                        applicationContext,
                        "Error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onSameThread()
                .check()
        } else {
            // Handle permissions for versions below Android 11 as needed
            Dexter.withContext(
                this@HomeActivity
            )
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        report.areAllPermissionsGranted() //

                        if (report.isAnyPermissionPermanentlyDenied) {
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                        Utils.isAdAlreadyOpen = true
                    }
                })
                .withErrorListener {
                    forOpenAd()
                    Toast.makeText(applicationContext, "Error occurred!", Toast.LENGTH_SHORT).show()
                }
                .onSameThread()
                .check()
        }
    }
    private fun checkButtonsPermissions(activity: Class<*>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isReadMediaVideoPermissionGranted &&
                isReadMediaAudioPermissionGranted && isReadMediaImagesPermissionGranted
            ) {
                AdUtils.showHomeInterstitialAd(this){
                    startActivity(Intent(this, activity))
                }
            } else {
                showPermissionDeniedToast()
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            if (isReadExternalPermissionGranted) {
                AdUtils.showHomeInterstitialAd(this){
                    startActivity(Intent(this, activity))
                }
            } else {
                showPermissionDeniedToast()
            }
        } else {
            if (isWriteExternalPermissionGranted && isReadExternalPermissionGranted) {
                AdUtils.showHomeInterstitialAd(this){
                    startActivity(Intent(this, activity))
                }
            } else {
                showPermissionDeniedToast()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
      /*  try {
            AdUtils.nativeAdApp=null
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    private fun showPermissionDeniedToast() {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }


    private val isReadExternalPermissionGranted: Boolean
        // Read , Write Storage Checking
        get() = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private val isWriteExternalPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    @get:RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val isReadMediaImagesPermissionGranted: Boolean
        // Storage permission check android 13
        get() = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

    @get:RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val isReadMediaAudioPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

    @get:RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val isReadMediaVideoPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_VIDEO
        ) == PackageManager.PERMISSION_GRANTED

    fun forOpenAd() {
        Handler(Looper.getMainLooper()).postDelayed({
            isOpen = true
            Utils.isAdAlreadyOpen = false
        }, 1500)
    } //    @Override

    //    public void onBackPressed() {
    //        System.exit(1);
    //    }
    companion object {
        var isOpen: Boolean = false

        private const val TAG = "interstitial add"
    }
}