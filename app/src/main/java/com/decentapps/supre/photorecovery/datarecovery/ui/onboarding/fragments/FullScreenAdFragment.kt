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
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.MediaView

class FullScreenAdFragment : Fragment() {
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
                loadFullScreenNativeAd(activity, BuildConfig.native_ob_full_scr_1,it)
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
        if(AdUtils.hfnativeAdLarge!=null){
            AdUtils.hfnativeAdLarge?.let { nativeAd ->
                val inflater = LayoutInflater.from(context)
                val adView = inflater.inflate(R.layout.fullscreen_native, null) as NativeAdView

                // Find views
                val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                val bodyView = adView.findViewById<TextView>(R.id.ad_body)
                val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
                val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)

                // Assign views to NativeAdView
                adView.mediaView = mediaView
                adView.headlineView = headlineView
                adView.bodyView = bodyView
                adView.callToActionView = callToActionView
                adView.iconView = iconView

                // Set media content (image or video)
                mediaView.setMediaContent(nativeAd.mediaContent)

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


        }
        else{
            if (AdUtils.nativeAdLarge == null) {
                val adLoader = AdLoader.Builder(context, adUnitId)
                    .forNativeAd { nativeAd ->
                        // Optional: Save for reuse
                        AdUtils.nativeAdLarge = nativeAd

                        val inflater = LayoutInflater.from(context)
                        val adView = inflater.inflate(R.layout.fullscreen_native, null) as NativeAdView

                        // Find views
                        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
                        val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
                        val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)

                        // Set view references to NativeAdView
                        adView.mediaView = mediaView
                        adView.headlineView = headlineView
                        adView.bodyView = bodyView
                        adView.callToActionView = callToActionView
                        adView.iconView = iconView

                        // Set media content (image or video)
                        mediaView.setMediaContent(nativeAd.mediaContent)

                        // Handle video callbacks
                        nativeAd.mediaContent?.videoController?.videoLifecycleCallbacks =
                            object : VideoController.VideoLifecycleCallbacks() {
                                override fun onVideoPlay() {
                                    Log.d("AdVideo", "Video is playing")
                                }

                                override fun onVideoEnd() {
                                    Log.d("AdVideo", "Video has ended")
                                }
                            }

                        // Bind content safely
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

                        // Always set native ad BEFORE adding to container
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

            else{
                AdUtils.nativeAdLarge?.let { nativeAd ->
                    val inflater = LayoutInflater.from(context)
                    val adView = inflater.inflate(R.layout.fullscreen_native, null) as NativeAdView

                    // Find views
                    val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                    val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                    val bodyView = adView.findViewById<TextView>(R.id.ad_body)
                    val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
                    val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)

                    // Assign views to NativeAdView
                    adView.mediaView = mediaView
                    adView.headlineView = headlineView
                    adView.bodyView = bodyView
                    adView.callToActionView = callToActionView
                    adView.iconView = iconView

                    // Set media content (image or video)
                    mediaView.setMediaContent(nativeAd.mediaContent)

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

            }

        }

    }

}
