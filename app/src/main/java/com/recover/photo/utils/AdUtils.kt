package com.recover.photo.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

object AdUtils {
    var adCounter:Int=0
    var threashold:Int=2
    var openAdCounter:Int=0
    var openAdThreashold:Int=2
    var nativeAdApp:NativeAd?=null
    var nativeAdOB1:NativeAd?=null
    var nativeAdOB2:NativeAd?=null
    var nativeAdOB3:NativeAd?=null
    var nativeAdLarge:NativeAd?=null
    var nativeAdLarge2:NativeAd?=null
    fun loadInterstitial(){

    }
    fun loadNative(context: Context,adUnitId: String){
        if(nativeAdApp!=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdApp=nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdOB1(context: Context, adUnitId: String) {
        if(nativeAdOB1!=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdOB1=nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdOB2(context: Context, adUnitId: String) {
        if(nativeAdOB2!=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdOB2=nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdOB3(context: Context, adUnitId: String) {
        if(nativeAdOB3!=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdOB3=nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdLarge(context: Context, adUnitId: String) {
        if(nativeAdLarge!=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdLarge=nativeAd
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e("NativeAd", "Failed to load: ${error.message}")
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
    fun loadFullScreenNativeAdLarge2(context: Context, adUnitId: String) {
        if(nativeAdLarge2!=null){
            return
        }
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                nativeAdLarge2=nativeAd
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