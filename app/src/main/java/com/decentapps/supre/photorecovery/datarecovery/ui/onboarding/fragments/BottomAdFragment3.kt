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
import com.decentapps.supre.photorecovery.datarecovery.databinding.FragmentBottomAd3Binding
import com.decentapps.supre.photorecovery.datarecovery.ui.onboarding.OnboardingActivity
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.MediaView

class BottomAdFragment3 : Fragment() {
    var mActivity:FragmentActivity?=null
    private val binding by lazy { FragmentBottomAd3Binding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mActivity?.let {
            binding.next.setOnClickListener {
                (activity as? OnboardingActivity)?.navigateNext()
            }
        }
        mActivity?.let {activity->
            binding?.adContainer?.let {
                loadFullScreenNativeAd(activity, BuildConfig.native_ob_3,it)
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
        if(AdUtils.hfnativeAdOB3!=null){
            AdUtils.hfnativeAdOB3?.let { nativeAd->
                AdUtils.populateNativeMedium(nativeAd,context,container)
            }

        }
        else{
            if(AdUtils.nativeAdOB3!=null){
                AdUtils.nativeAdOB3?.let { nativeAd->
                    AdUtils.populateNativeMedium(nativeAd,context,container)
                }
            }
            else{
                val adLoader = AdLoader.Builder(context, adUnitId)
                    .forNativeAd { nativeAd ->
                        AdUtils.populateNativeMedium(nativeAd,context,container)
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
