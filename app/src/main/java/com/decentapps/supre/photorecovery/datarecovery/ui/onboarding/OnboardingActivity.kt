package com.decentapps.supre.photorecovery.datarecovery.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.databinding.ActivityOnboardingBinding
import com.decentapps.supre.photorecovery.datarecovery.ui.activity.HomeActivity
import com.decentapps.supre.photorecovery.datarecovery.ui.onboarding.adapter.OnboardingPagerAdapter
import com.decentapps.supre.photorecovery.datarecovery.utils.SharedPrefHelper

class OnboardingActivity : AppCompatActivity(){
    private val binding by lazy { ActivityOnboardingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val adapter = OnboardingPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val window = this@OnboardingActivity.window
                when (position) {
                    0, 2, 4 -> { // 1st, 3rd, 5th fragments (0-based index)
                        window.statusBarColor = ContextCompat.getColor(this@OnboardingActivity, R.color.status_highlight)
                        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
                    }
                    else -> {
                        window.statusBarColor = ContextCompat.getColor(this@OnboardingActivity, android.R.color.white)
                        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
                    }
                }
            }
        })

    }
    fun navigateNext(){
        val next = binding.viewPager.currentItem + 1
        if (next < 5) {
            binding.viewPager.currentItem = next
        } else {
            SharedPrefHelper.setNotFirstTime(this)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}

