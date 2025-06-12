package com.decentapps.supre.photorecovery.datarecovery.ui.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.decentapps.supre.photorecovery.datarecovery.BuildConfig
import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.MobileAds
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.ui.onboarding.OnboardingActivity
import com.decentapps.supre.photorecovery.datarecovery.utils.AdUtils
import com.decentapps.supre.photorecovery.datarecovery.utils.AppUtils.privacypolicy
import com.decentapps.supre.photorecovery.datarecovery.utils.SharedPrefHelper
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import java.util.concurrent.atomic.AtomicBoolean

class SplashScreen : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var agreementLayout: ConstraintLayout
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var rlOuter: ConstraintLayout? = null
    private var cbAgree: CheckBox? = null
    private var tvPrivacyPolicy: TextView? = null
    private var btnAgree: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     /*   this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)*/
        setContentView(R.layout.splash_screen)
        agreementLayout = findViewById(R.id.rlOuter)
        initializeMobileAdsSdk()
        agreement()
        init()
        listeners()
        Utils.loadInters(this)
    }
    private fun initializeMobileAdsSdk() {
      /*  if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }*/

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {
            if(SharedPrefHelper.isFirstTime(this)){
                AdUtils.loadNativeAdOB1Hf(this,BuildConfig.native_ob1_hf)
                AdUtils.loadNativeAdOB2Hf(this,BuildConfig.native_ob2_hf)
                AdUtils.loadNativeAdOB3Hf(this,BuildConfig.native_ob_3_hf)
                AdUtils.loadFullScreenNativeAdLargeHf(this,BuildConfig.native_ob_full_scr_1_hf)
                AdUtils.loadFullScreenNativeAdLarge2Hf(this,BuildConfig.native_ob_full_scr_2_hf)
            }
            AdUtils.loadNative(this,BuildConfig.native_ob_full_scr_1_hf)
        }

        // Load an ad.
//        (application as MyApplication).loadAd(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
    private fun agreement() {
        val isAgree = PreferenceManager.getDefaultSharedPreferences(
            applicationContext
        ).getBoolean("Agreed", false)
        if (isAgree) {
            rlOuter?.visibility=View.INVISIBLE
            handler.postDelayed({
                showOpenAd()
            }, 5000)

        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                agreementLayout.visibility = View.VISIBLE
            }, 3000)
        }
    }

    private fun init() {
        rlOuter = findViewById(R.id.rlOuter)
        cbAgree = findViewById(R.id.cbAgree)
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy)
        btnAgree = findViewById(R.id.btnAgree)
        val fullText = getString(R.string.i_have_read_and_agree) + " " + getString(R.string.privacy_policy)
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf(getString(R.string.privacy_policy))
        val end = start + getString(R.string.privacy_policy).length

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                privacypolicy(this@SplashScreen)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = ContextCompat.getColor(this@SplashScreen, R.color.colorBlue)
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvPrivacyPolicy?.text = spannable
        tvPrivacyPolicy?.movementMethod = LinkMovementMethod.getInstance()
        tvPrivacyPolicy?.highlightColor = Color.TRANSPARENT
    }

    private fun listeners() {
        cbAgree?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btnAgree?.visibility = View.VISIBLE
            } else {
                btnAgree?.visibility = View.INVISIBLE
            }
        }

        btnAgree?.setOnClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(
                applicationContext
            )
            val editor = prefs.edit()
            editor.putBoolean("Agreed", true)
            editor.apply()
            editor.commit()
            agreementLayout.visibility=View.INVISIBLE
            rlOuter?.visibility=View.INVISIBLE
            handler.postDelayed({
                showOpenAd()
            }, 300)
        }
    }
    private fun showOpenAd(){
        if(SharedPrefHelper.isFirstTime(this)){
//                   Handler(Looper.getMainLooper()).postDelayed({
                       startActivity(Intent(this@SplashScreen, OnboardingActivity::class.java))
                       finish()
//          }, 2500)

        }
        else{
     /*       Handler(Looper.getMainLooper()).postDelayed({

            }, 5000)*/
            startActivity(Intent(this@SplashScreen, HomeActivity::class.java))
            finish()
        }
     /*   (application as MyApplication).showAdIfAvailable(
            this@SplashScreen,
            object : MyApplication.OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }
            }
        )*/
    }

}
