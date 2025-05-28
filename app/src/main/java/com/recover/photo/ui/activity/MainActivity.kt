package com.recover.photo.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.navigation.NavigationView
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateFailureListener
import com.google.android.ump.ConsentInformation.OnConsentInfoUpdateSuccessListener
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.recover.photo.R
import com.recover.photo.databinding.ActivityMainBinding
import com.recover.photo.utils.AppUtils.moreApps
import com.recover.photo.utils.AppUtils.privacypolicy
import com.recover.photo.utils.AppUtils.rateApp
import com.recover.photo.utils.AppUtils.shareApp
import com.recover.photo.utils.Utils
import com.recover.photo.utils.percentage
import com.recover.photo.utils.storageValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.log10
import kotlin.math.pow

class MainActivity : AppCompatActivity(), View.OnClickListener {
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
        getStorage()
        sharedPreferencesAudio = getSharedPreferences(prefAudio, MODE_PRIVATE)
        sharedPreferencesVideo = getSharedPreferences(prefVideo, MODE_PRIVATE)
        Utils.isAdAlreadyOpen = true

        isUnlockAudio = sharedPreferencesAudio?.getBoolean(prefAudio, false) == true
        isUnlockVideo = sharedPreferencesVideo?.getBoolean(prefVideo, false) == true
        checkIsUnlock()
        gdprSetUp()
        pd = ProgressDialog(this)
        pd?.setTitle("Loading Ad")
        pd?.setMessage("Please Wait")
        findViewById<View>(R.id.iconVid).visibility = View.GONE
        findViewById<View>(R.id.iconAudio).visibility = View.GONE
        listeners()
        checkPermissions()
    }

   /* private fun navigationDrawer() {
        // Enable the drawer toggle
        binding.drawerIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        val navView: NavigationView = binding.navDrawer
        val rateus = navView.findViewById<ConstraintLayout>(R.id.rateusdrawer)
        val privacy = navView.findViewById<ConstraintLayout>(R.id.privacydrawer)
        val share = navView.findViewById<ConstraintLayout>(R.id.share)
        val moreapps = navView.findViewById<ConstraintLayout>(R.id.moreApps)
        rateus.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            rateApp(this)
        }
        privacy.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            privacypolicy(this)
        }
        share.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
//            shareApp(AppLinkModel)
            shareApp(this)
        }
        moreapps.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            moreApps(this)
//            startActivity(Intent(this@MainActivity, AppSettingsActivity::class.java))
        }
//        val versionName: String = BuildConfig.VERSION_NAME
      *//*  appversion.text="${versionName}"
        moreapps.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(applicationContext, AppLanguageActivity::class.java)
            intent.putExtra("languageMainValues", true)
            startActivity(intent)
        }*//*
    }*/


    private fun checkIsUnlock() {
        if (isUnlockVideo) {
            findViewById<View>(R.id.iconVid).visibility = View.GONE
        } else {
            findViewById<View>(R.id.iconVid).visibility = View.VISIBLE
        }

        if (isUnlockAudio) {
            findViewById<View>(R.id.iconAudio).visibility = View.GONE
        } else {
            findViewById<View>(R.id.iconAudio).visibility = View.VISIBLE
        }
    }

    private fun unlockVideo() {
        saveValueBool(sharedPreferencesVideo, prefVideo, true)
        checkButtonsPermissions(1)
        checkIsUnlock()
    }

    private fun unlockAudio() {
        saveValueBool(sharedPreferencesAudio, prefAudio, true)
        checkButtonsPermissions(2)
        checkIsUnlock()
    }


    private fun saveValueBool(sharedPreferences: SharedPreferences?, key: String, value: Boolean) {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }


    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.scanImgs -> {
                clickPosition = 1
                if (AdCounter.AdsCounter >= 2) {
                    checkButtonsPermissions(0)
                } else {
                    AdCounter.AdsCounter++
                    checkButtonsPermissions(0)
                }
            }

            R.id.scanVideos -> if (isUnlockVideo) {
                findViewById<View>(R.id.iconVid).visibility = View.GONE
                clickPosition = 2
                if (AdCounter.AdsCounter >= 2) {
                    checkButtonsPermissions(1)
                } else {
                    AdCounter.AdsCounter++
                    checkButtonsPermissions(1)
                }
            } else {
                findViewById<View>(R.id.iconVid).visibility = View.VISIBLE
                unlockVideo()
            }


            R.id.scanAudios -> if (isUnlockAudio) {
                findViewById<View>(R.id.iconAudio).visibility = View.GONE
                clickPosition = 3
                if (AdCounter.AdsCounter >= 2) {
                    checkButtonsPermissions(2)
                } else {
                    AdCounter.AdsCounter++
                    checkButtonsPermissions(2)
                }
            } else {
                findViewById<View>(R.id.iconAudio).visibility = View.VISIBLE
                unlockAudio()
            }

            R.id.recoveredImgs -> if (AdCounter.AdsCounter >= 3) {
                checkButtonsPermissions(RecoveredImagesActivity::class.java)
            } else {
                AdCounter.AdsCounter++
                checkButtonsPermissions(RecoveredImagesActivity::class.java)
            }

            R.id.recoveredAudios -> if (AdCounter.AdsCounter >= 2) {
                checkButtonsPermissions(RecoveredAudiosActivity::class.java)
            } else {
                AdCounter.AdsCounter++
                checkButtonsPermissions(RecoveredAudiosActivity::class.java)
            }

            R.id.recoveredVideos -> if (AdCounter.AdsCounter >= 2) {
                checkButtonsPermissions(RecoveredVideosActivity::class.java)
            } else {
                AdCounter.AdsCounter++
                checkButtonsPermissions(RecoveredVideosActivity::class.java)
            }
        }
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



/*    private fun showBannerAd() {
        val mAdView = findViewById<AdView>(R.id.adView)
        Utils.showBannerAd(mAdView)
    }*/

    private fun listeners() {
        binding.scanImgs.setOnClickListener(this)
        binding.scanVideos.setOnClickListener(this)
        binding.scanAudios.setOnClickListener(this)
        binding.recoveredImgs.setOnClickListener(this)
        binding.recoveredVideos.setOnClickListener(this)
        binding.recoveredAudios.setOnClickListener(this)
        binding.images.setOnClickListener { checkButtonsPermissions(0) }
        binding.video.setOnClickListener {
            checkButtonsPermissions(1)
        }
        binding.audio.setOnClickListener {
            checkButtonsPermissions(2)
        }
        binding.settings.setOnClickListener {
            checkButtonsPermissions(RecoveredFilesActivity::class.java)
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
        binding.totalTitle.text = usedSpace+" of "+total+" Used"
        storageValue =usedSpace+" of "+total+" Used"
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