package com.decentapps.supre.photorecovery.datarecovery.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.decentapps.supre.photorecovery.datarecovery.BuildConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.decentapps.supre.photorecovery.datarecovery.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdUtils {
    var adCounter:Int=0
    var threashold:Int=2
    var openAdCounter:Int=0
    var openAdThreashold:Int=2
    var nativeAdApp:NativeAd?=null
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
                Log.d("Admob", "Interstitial ad loaded.")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                homeInterstitialAd = null
                if(!secondRequestHome) {
                    requestHomeInterstitialAd(context, adUnitId)
                    secondRequestHome=true
                }
                Log.e("Admob", "Failed to load interstitial ad: ${adError.message}")
            }
        })
    }

    fun showSplashInterstitialAd(activity: Activity, onAdFinished: () -> Unit) {
        if (splashInterstitialAd != null) {
            splashInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("Admob", "Ad was dismissed.")
                    splashInterstitialAd = null
                }
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("Admob", "Ad failed to show.")
                    splashInterstitialAd = null
                    onAdFinished()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("Admob", "Ad showed fullscreen content.")
                }
            }
            onAdFinished()
            splashInterstitialAd?.show(activity)
        } else {
            Log.d("Admob", "Ad is not ready.")
            onAdFinished()
        }
    }
    fun showHomeInterstitialAd(activity: Activity, onAdFinished: () -> Unit) {
        if (splashInterstitialAd != null) {
            splashInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("Admob", "Ad was dismissed.")
                    splashInterstitialAd = null
                }
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("Admob", "Ad failed to show.")
                    splashInterstitialAd = null
                    onAdFinished()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("Admob", "Ad showed fullscreen content.")
                }
            }
            onAdFinished()
            splashInterstitialAd?.show(activity)
        } else {
            requestHomeInterstitialAd(activity as Context,BuildConfig.home_interstitial)
            Log.d("Admob", "Ad is not ready.")
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