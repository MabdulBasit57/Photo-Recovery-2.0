package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.android.ump.ConsentInformation
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.databinding.ActivityMainBinding
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils
import com.decentapps.supre.photorecovery.datarecovery.utils.AppUtils.filter
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
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.log10
import kotlin.math.pow

class MainActivity : AppCompatActivity(){
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val PERMISSION_REQUEST_CODE = 23
    var clickPosition: Int = 0
    var consentInformation: ConsentInformation? = null
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
//    private var rewardedAd: RewardedAd? = null

    var pd: ProgressDialog? = null


    private val prefAudio = "my_pref_audio"
    private val prefVideo = "my_pref_video"

    var sharedPreferencesAudio: SharedPreferences? = null
    var sharedPreferencesVideo: SharedPreferences? = null
    var isUnlockAudio: Boolean = false
    var isUnlockVideo: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Initialize views
//        navigationDrawer()
        AdUtils.requestHomeInterstitialAd(this, BuildConfig.home_interstitial)
        try {
            binding?.adContainer?.let { showTempelateNativeAd(this,BuildConfig.native_home, it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getStorage()
        sharedPreferencesAudio = getSharedPreferences(prefAudio, MODE_PRIVATE)
        sharedPreferencesVideo = getSharedPreferences(prefVideo, MODE_PRIVATE)

        isUnlockAudio = sharedPreferencesAudio?.getBoolean(prefAudio, false) == true
        isUnlockVideo = sharedPreferencesVideo?.getBoolean(prefVideo, false) == true
        gdprSetUp()
        pd = ProgressDialog(this)
        pd?.setTitle("Loading Ad")
        pd?.setMessage("Please Wait")
        listeners()
        checkPermissions()
    }

    private fun unlockVideo() {
        saveValueBool(sharedPreferencesVideo, prefVideo, true)
        checkButtonsPermissions(1)
    }

    private fun unlockAudio() {
        saveValueBool(sharedPreferencesAudio, prefAudio, true)
        checkButtonsPermissions(2)
    }


    private fun saveValueBool(sharedPreferences: SharedPreferences?, key: String, value: Boolean) {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }


    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this@MainActivity)
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
                this@MainActivity
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

/*    private fun showBannerAd() {
        val mAdView = findViewById<AdView>(R.id.adView)
        Utils.showBannerAd(mAdView)
    }*/

    private fun listeners() {
        binding.images.setOnClickListener {
            showRecoverDialog("Do You want to recover your photos?",0,"photo")
//            checkButtonsPermissions(0)
        }
        binding.video.setOnClickListener {
            showRecoverDialog("Do You want to recover your videos?",1,"video")
//            checkButtonsPermissions(1)
        }
        binding.audio.setOnClickListener {
            showRecoverDialog("Do You want to recover your audios?",2,"audio")
//            checkButtonsPermissions(2)
        }
        binding.settings.setOnClickListener {
            checkButtonsPermissions(AboutActivity::class.java)
        }
        binding.customToolbar.backToolbar.setOnClickListener {
            onBackPressed()
        }
        binding.customToolbar.titleToolbar.text="Data Recovery"
    }

    private fun gdprSetUp() {
  /*      val params =
            ConsentRequestParameters.Builder() //                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        //        consentInformation.reset();// remove in production
        consentInformation?.requestConsentInfoUpdate(
            this@MainActivity,
            params,
            OnConsentInfoUpdateSuccessListener {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this,
                    OnConsentFormDismissedListener { loadAndShowError: FormError? ->
                        if (loadAndShowError != null) {
                            // Consent gathering failed.
//                                    Log.w(TAG, String.format("%s: %s",
//                                            loadAndShowError.getErrorCode(),
//                                            loadAndShowError.getMessage()));
                        }
                        // Consent has been gathered.
                        if (consentInformation?.canRequestAds() == true) {
                            initializeMobileAdsSdk()
                        }
                    }
                )
            },
            OnConsentInfoUpdateFailureListener { requestConsentError: FormError? -> })

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation?.canRequestAds() == true) {
            initializeMobileAdsSdk()
        }*/
    }
    fun showRecoverDialog(msg: String, pos: Int, title: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.confirm_dialog)
        dialog.setCancelable(true)

        val btnYes = dialog.findViewById<TextView>(R.id.btnYes)
        val btnNo = dialog.findViewById<TextView>(R.id.btnNo)
        val icon = dialog.findViewById<ImageView>(R.id.imgPhoto)
        val message = dialog.findViewById<TextView>(R.id.tvMessage)
        message.text = msg

        val tv7Days = dialog.findViewById<TextView>(R.id.btn7Days)
        val tv30Days = dialog.findViewById<TextView>(R.id.btn30Days)
        val tvLifetime = dialog.findViewById<TextView>(R.id.btnLifetime)

        var selectedFilterDays: Int? = null

        fun selectFilter(selected: TextView, days: Int) {
            tv7Days.setBackgroundResource(R.drawable.btn_filter_bg)
            tv30Days.setBackgroundResource(R.drawable.btn_filter_bg)
            tvLifetime.setBackgroundResource(R.drawable.btn_filter_bg)

            selected.setBackgroundResource(R.drawable.bg_filter_bg_selected)
            selected.setTextColor(resources.getColor(R.color.white))
            selectedFilterDays = days
            filter=days

            btnYes.isEnabled = true
            btnYes.alpha = 1f
        }

        // Initial disable
//        btnYes.isEnabled = false
        btnYes.alpha = 0.3f

        // Filter option clicks
        tv7Days.setOnClickListener { selectFilter(tv7Days, 7)
            tv30Days.setTextColor(resources.getColor(R.color.black))
            tvLifetime.setTextColor(resources.getColor(R.color.black))
        }
        tv30Days.setOnClickListener { selectFilter(tv30Days, 30)
            tv7Days.setTextColor(resources.getColor(R.color.black))
            tvLifetime.setTextColor(resources.getColor(R.color.black))}
        tvLifetime.setOnClickListener { selectFilter(tvLifetime, 0)
            tv30Days.setTextColor(resources.getColor(R.color.black))
            tv7Days.setTextColor(resources.getColor(R.color.black))}

        btnYes.setOnClickListener {
            if (selectedFilterDays == null) {
                Toast.makeText(this, "Please select a filter option", Toast.LENGTH_SHORT).show()
            } else {
                checkButtonsPermissions(pos)
                dialog.dismiss()
            }
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        // Load icon
        val iconRes = when (title) {
            "photo" -> R.drawable.photo_svg_main
            "video" -> R.drawable.video_main_svg
            "audio" -> R.drawable.audio_main_svg
            else -> R.drawable.photo_svg_main
        }

        Glide.with(this)
            .load(iconRes)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    icon.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }



    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Google Mobile Ads SDK.
//        MobileAds.initialize(this)

        // TODO: Request an ad.
//        loadInters();
//        showBannerAd()
    }

    private fun checkButtonsPermissions(pos: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isReadMediaVideoPermissionGranted &&
                isReadMediaAudioPermissionGranted && isReadMediaImagesPermissionGranted
            ) {
                AdUtils.showHomeInterstitialAd(this){
                    val intent = Intent(this, ScanImagesActivty::class.java)
                    intent.putExtra("position", pos)
                    startActivity(intent)
                }

            } else {
                showPermissionDeniedToast()
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            if (isReadExternalPermissionGranted) {
                AdUtils.showHomeInterstitialAd(this) {
                    val intent = Intent(this, ScanImagesActivty::class.java)
                    intent.putExtra("position", pos)
                    startActivity(intent)
                }
            } else {
                showPermissionDeniedToast()
            }
        } else {
            if (isWriteExternalPermissionGranted && isReadExternalPermissionGranted) {
                AdUtils.showHomeInterstitialAd(this) {
                    val intent = Intent(this, ScanImagesActivty::class.java)
                    intent.putExtra("position", pos)
                    startActivity(intent)
                }
            } else {
                showPermissionDeniedToast()
            }
        }
    }

    private fun checkButtonsPermissions(activity: Class<*>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isReadMediaVideoPermissionGranted &&
                isReadMediaAudioPermissionGranted && isReadMediaImagesPermissionGranted
            ) {
                startActivity(Intent(this, activity))
            } else {
                showPermissionDeniedToast()
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
            if (isReadExternalPermissionGranted) {
                startActivity(Intent(this, activity))
            } else {
                showPermissionDeniedToast()
            }
        } else {
            if (isWriteExternalPermissionGranted && isReadExternalPermissionGranted) {
                startActivity(Intent(this, activity))
            } else {
                showPermissionDeniedToast()
            }
        }
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
}