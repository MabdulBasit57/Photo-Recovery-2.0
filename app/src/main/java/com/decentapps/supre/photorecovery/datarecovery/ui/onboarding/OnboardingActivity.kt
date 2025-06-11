package com.decentapps.supre.photorecovery.datarecovery.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

