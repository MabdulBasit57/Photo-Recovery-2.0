package com.decentapps.supre.photorecovery.datarecovery.ui.onboarding.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.decentapps.supre.photorecovery.datarecovery.BuildConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdView
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.databinding.FragmentFullscreenAdBinding
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils

class FullScreenAdFragment2 : Fragment() {

    var mActivity: FragmentActivity?=null
    private val binding by lazy { FragmentFullscreenAdBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mActivity?.let {activity->
            binding?.frameLayout?.let {
                loadFullScreenNativeAd(activity, BuildConfig.native_ob_full_scr_2,it)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mActivity=null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity=requireActivity()
    }
    fun loadFullScreenNativeAd(context: Context, adUnitId: String, container: FrameLayout) {
        if(AdUtils.hfnativeAdLarge2!=null){
            AdUtils.hfnativeAdLarge2?.let { nativeAd->
                val inflater = LayoutInflater.from(context)
                val adView = inflater.inflate(R.layout.fullscreen_native, null) as NativeAdView
                // Set views
                adView.mediaView = adView.findViewById(R.id.ad_media)
                adView.mediaView?.mediaContent = nativeAd.mediaContent
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

        }
        else{
            if(AdUtils.nativeAdLarge2!=null){
                AdUtils.nativeAdLarge2?.let { nativeAd->
                    val inflater = LayoutInflater.from(context)
                    val adView = inflater.inflate(R.layout.fullscreen_native, null) as NativeAdView
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
            }
            else{
                val adLoader = AdLoader.Builder(context, adUnitId)
                    .forNativeAd { nativeAd ->
                        val inflater = LayoutInflater.from(context)
                        val adView = inflater.inflate(R.layout.fullscreen_native, null) as NativeAdView
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

    }
}
