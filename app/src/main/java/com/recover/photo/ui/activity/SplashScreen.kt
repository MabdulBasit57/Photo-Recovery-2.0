package com.recover.photo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.Html
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
//import com.google.android.gms.ads.MobileAds
import com.recover.photo.R
import com.recover.photo.ui.onboarding.OnboardingActivity
import com.recover.photo.utils.SharedPrefHelper
import com.recover.photo.utils.Utils
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
//        MobileAds.initialize(this) {}

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
            }, 3000)

        } else {
            agreementLayout.visibility=View.VISIBLE
            val txtTermsCondition =
                resources.getString(R.string.i_have_read_and_agree) + " <a href='" + resources.getString(
                    R.string.url_privacy
                ) + "'</a>" + resources.getString(R.string.privacy_policy)
            tvPrivacyPolicy?.setLinkTextColor(resources.getColor(R.color.colorContent))


            val s = Html.fromHtml(txtTermsCondition) as Spannable
            for (u in s.getSpans(0, s.length, URLSpan::class.java)) {
                s.setSpan(object : UnderlineSpan() {
                    override fun updateDrawState(tp: TextPaint) {
                        tp.isUnderlineText = false
                    }
                }, s.getSpanStart(u), s.getSpanEnd(u), 0)
            }
            tvPrivacyPolicy?.text = s

            //        tvTerms.setText(Html.fromHtml(txtTermsCondition));
            tvPrivacyPolicy?.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun init() {
        rlOuter = findViewById(R.id.rlOuter)
        cbAgree = findViewById(R.id.cbAgree)
        tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy)
        btnAgree = findViewById(R.id.btnAgree)
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
            }, 3000)
        }
    }
    private fun showOpenAd(){
        if(SharedPrefHelper.isFirstTime(this)){
            startActivity(Intent(this@SplashScreen, OnboardingActivity::class.java))
            finish()
        }
        else{
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
