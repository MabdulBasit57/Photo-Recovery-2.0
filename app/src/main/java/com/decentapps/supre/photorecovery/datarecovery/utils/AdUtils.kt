package com.decentapps.supre.photorecovery.datarecovery.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.decentapps.supre.photorecovery.datarecovery.BuildConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils.isAdAlreadyOpen
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

object AdUtils {
    var adCounter:Int=0
    var threashold:Int=2
    var openAdCounter:Int=0
    var openAdThreashold:Int=2
    var nativeAdApp:NativeAd?=null
    var nativeAdAppNormal:NativeAd?=null
    var nativeAdAppSetting:NativeAd?=null
    var nativeAdAppSettingNormal:NativeAd?=null
    var nativeAdOB1:NativeAd?=null
    var hfnativeAdOB1:NativeAd?=null
    var nativeAdOB2:NativeAd?=null
    var hfnativeAdOB2:NativeAd?=null
    var nativeAdOB3:NativeAd?=null
    var hfnativeAdOB3:NativeAd?=null
    var nativeAdLarge:NativeAd?=null
    var hfnativeAdLarge:NativeAd?=null
    var nativeAdLarge2:NativeAd?=null
    var hfnativeAdLarge2:NativeAd?=null
    var splashInterstitialAd: InterstitialAd? = null
    var homeInterstitialAd: InterstitialAd? = null
    var secondRequest=false
    var secondRequestHome=false
    private var adLoadingDialog: Dialog? = null
    fun showAdLoadingDialog(activity: Activity) {
        if (adLoadingDialog?.isShowing == true) return
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.ad_dialog, null)
        adLoadingDialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            setContentView(dialogView)
            setCancelable(false)
            show()
        }
    }
    fun dismissAdLoadingDialog() {
        adLoadingDialog?.dismiss()
        adLoadingDialog = null
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun requestSplashInterstitialAd(context: Context) {

        if(splashInterstitialAd!=null)
            return
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, BuildConfig.splash_interstitial, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                splashInterstitialAd = ad
                splashAdLoaded?.postValue(true)
                secondRequest=false
                Log.d("Admob", "Interstitial ad loaded.")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                splashInterstitialAd = null
                if(!secondRequest) {
                    requestSplashInterstitialAd(context)
                    secondRequest=true
                }
                else{
                    splashAdLoaded?.postValue(true)
                }
                Log.e("Admob", "Failed to load interstitial ad: ${adError.message}")
            }
        })
    }
    fun requestHomeInterstitialAd(context: Context, adUnitId: String) {

        if(homeInterstitialAd!=null)
            return
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                homeInterstitialAd = ad
                secondRequestHome=false
                Log.w("Admob", "Interstitial ad loaded Home.")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                homeInterstitialAd = null
                if(!secondRequestHome) {
                    requestHomeInterstitialAd(context, adUnitId)
                    secondRequestHome=true
                }
                Log.e("Admob", "Failed to load interstitial ad Home: ${adError.message}")
            }
        })
    }

    fun showSplashInterstitialAd(activity: Activity, onAdFinished: () -> Unit) {
        if (splashInterstitialAd != null) {
            showAdLoadingDialog(activity)
            isAdAlreadyOpen=true
            splashInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("Admob", "Ad was dismissed.")
                    splashInterstitialAd = null
                    dismissAdLoadingDialog()
                    isAdAlreadyOpen=false
                }
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("Admob", "Ad failed to show.")
                    splashInterstitialAd = null
                    onAdFinished()
                    isAdAlreadyOpen=false
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("Admob", "Ad showed fullscreen content.")
                }
            }

//            Handler(Looper.getMainLooper()).postDelayed({
                onAdFinished()
                splashInterstitialAd?.show(activity)
//                dismissAdLoadingDialog()
//            },1500)

        } else {
            Log.d("Admob", "Ad is not ready.")
            onAdFinished()
        }
    }
    fun showHomeInterstitialAd(activity: Activity, onAdFinished: () -> Unit) {
        if (homeInterstitialAd != null) {
            showAdLoadingDialog(activity)
            isAdAlreadyOpen=true
            homeInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("Admob", "Ad was dismissed.")
                    homeInterstitialAd = null
                    dismissAdLoadingDialog()
                    isAdAlreadyOpen=false
                    secondRequestHome=false
                    requestHomeInterstitialAd(activity as Context,BuildConfig.home_interstitial)
                }
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("Admob", "Ad failed to show Home.")
                    homeInterstitialAd = null
                    onAdFinished()
                    isAdAlreadyOpen=false
                }
                override fun onAdShowedFullScreenContent() {
                    Log.d("Admob", "Ad showed fullscreen content.")
                }
            }

//            Handler(Looper.getMainLooper()).postDelayed({
                onAdFinished()
                homeInterstitialAd?.show(activity)
//                dismissAdLoadingDialog()
//            },1500)
        } else {
            requestHomeInterstitialAd(activity as Context,BuildConfig.home_interstitial)
            Log.e("Admob", "Ad is not ready.")
            onAdFinished()
        }
    }

    fun loadNative(context: Context,adUnitId: String){
        if(nativeAdApp !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdApp =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                    loadNativenormal(context,BuildConfig.native_home)
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun populateNativeLarge(nativeAd: NativeAd,context: Context,container: FrameLayout){
        try {
            val inflater = LayoutInflater.from(context)
            val adView = inflater.inflate(R.layout.fullscreen_native, null) as NativeAdView

            // Find views
            val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            val bodyView = adView.findViewById<TextView>(R.id.ad_body)
            val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
            val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)


            adView.headlineView = headlineView
            adView.bodyView = bodyView
            adView.callToActionView = callToActionView
            adView.iconView = iconView

            // Set media content (image or video)
            mediaView.setMediaContent(nativeAd.mediaContent)
            // Assign views to NativeAdView
            adView.mediaView = mediaView
            // Optional: video lifecycle callbacks
            nativeAd.mediaContent?.videoController?.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoPlay() {
                        Log.d("AdVideo", "Video is playing")
                    }

                    override fun onVideoEnd() {
                        Log.d("AdVideo", "Video has ended")
                    }
                }

            // Bind content
            headlineView.text = nativeAd.headline ?: ""
            nativeAd.body?.let {
                bodyView.text = it
                bodyView.visibility = View.VISIBLE
            } ?: run {
                bodyView.visibility = View.GONE
            }

            nativeAd.callToAction?.let {
                callToActionView.text = it
                callToActionView.visibility = View.VISIBLE
            } ?: run {
                callToActionView.visibility = View.GONE
            }

            nativeAd.icon?.let {
                iconView.setImageDrawable(it.drawable)
                iconView.visibility = View.VISIBLE
            } ?: run {
                iconView.visibility = View.GONE
            }

            // MUST: Set native ad to the adView before attaching to parent
            adView.setNativeAd(nativeAd)

            container.removeAllViews()
            container.addView(adView)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun populateNativeMedium(nativeAd: NativeAd,context: Context,container: FrameLayout){
        try {
            val inflater = LayoutInflater.from(context)
            val adView = inflater.inflate(R.layout.medium_native, null) as NativeAdView

            val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            val bodyView = adView.findViewById<TextView>(R.id.ad_body)
            val ctaView = adView.findViewById<Button>(R.id.ad_call_to_action)
            val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
            adView.headlineView = headlineView
            adView.bodyView = bodyView
            adView.callToActionView = ctaView
            adView.iconView = iconView

            headlineView.text = nativeAd.headline
            bodyView.text = nativeAd.body
            ctaView.text = nativeAd.callToAction

            val icon = nativeAd.icon
            if (icon != null) {
                iconView.setImageDrawable(icon.drawable)
                iconView.visibility = View.VISIBLE
            } else {
                iconView.visibility = View.GONE
            }

            // Set media content (image or video)
            mediaView.setMediaContent(nativeAd.mediaContent)
            // Assign views to NativeAdView
            adView.mediaView = mediaView

            val videoController = nativeAd.mediaContent?.videoController
            if (nativeAd.mediaContent?.hasVideoContent() == true) {
                videoController?.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                        // Optional: do something
                    }
                }
            }

            adView.setNativeAd(nativeAd)
            container.removeAllViews()
            container.addView(adView)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun populateNativeSmall(nativeAd: NativeAd,context: Context,container: FrameLayout){
        try {
            val inflater = LayoutInflater.from(context)
            val adView = inflater.inflate(R.layout.small_native, null) as NativeAdView

            val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            val bodyView = adView.findViewById<TextView>(R.id.ad_body)
            val ctaView = adView.findViewById<Button>(R.id.ad_call_to_action)
            val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)

            adView.headlineView = headlineView
            adView.bodyView = bodyView
            adView.callToActionView = ctaView
            adView.iconView = iconView

            headlineView.text = nativeAd.headline
            bodyView.text = nativeAd.body
            ctaView.text = nativeAd.callToAction

            val icon = nativeAd.icon
            if (icon != null) {
                iconView.setImageDrawable(icon.drawable)
                iconView.visibility = View.VISIBLE
            } else {
                iconView.visibility = View.GONE
            }

            // Set media content (image or video)
            mediaView.setMediaContent(nativeAd.mediaContent)
            // Assign views to NativeAdView
            adView.mediaView = mediaView

            val videoController = nativeAd.mediaContent?.videoController
            if (nativeAd.mediaContent?.hasVideoContent() == true) {
                videoController?.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                        // Optional: do something
                    }
                }
            }

            adView.setNativeAd(nativeAd)
            container.removeAllViews()
            container.addView(adView)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun loadNativenormal(context: Context,adUnitId: String){
        if(nativeAdAppNormal !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdAppNormal =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeSetting(context: Context,adUnitId: String){
        if(nativeAdAppSetting !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdAppSetting =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                    loadNativeSettingNormal(context,BuildConfig.native_setting)
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeSettingNormal(context: Context,adUnitId: String){
        if(nativeAdAppSettingNormal !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdAppSettingNormal =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeAdOB1Hf(context: Context, adUnitId: String) {
        if(hfnativeAdOB1 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                hfnativeAdOB1 =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                    loadNativeAdOB1(context, BuildConfig.native_ob1)
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeAdOB1(context: Context, adUnitId: String) {
        if(nativeAdOB1 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdOB1 =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeAdOB2Hf(context: Context, adUnitId: String) {
        if(hfnativeAdOB2 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                hfnativeAdOB2 =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                    loadNativeAdOB2(context,BuildConfig.native_ob2)
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeAdOB2(context: Context, adUnitId: String) {
        if(nativeAdOB2 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdOB2 =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeAdOB3Hf(context: Context, adUnitId: String) {
        if(hfnativeAdOB3 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                hfnativeAdOB3 =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadNativeAdOB3(context,BuildConfig.native_ob_3)

                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadNativeAdOB3(context: Context, adUnitId: String) {
        if(nativeAdOB3 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdOB3 =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdLargeHf(context: Context, adUnitId: String) {
        if(hfnativeAdLarge !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                hfnativeAdLarge =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadFullScreenNativeAdLarge(context,BuildConfig.native_ob_full_scr_1)
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdLarge(context: Context, adUnitId: String) {
        if(nativeAdLarge !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdLarge =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdLarge2Hf(context: Context, adUnitId: String) {
        if(hfnativeAdLarge2 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                hfnativeAdLarge2 =nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loadFullScreenNativeAdLarge2(context,BuildConfig.native_ob_full_scr_2)
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdLarge2(context: Context, adUnitId: String) {
        if(nativeAdLarge2 !=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdLarge2 =nativeAd
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