package com.recover.photo.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.recover.photo.databinding.ActivityOnboardingBinding
import com.recover.photo.ui.activity.HomeActivity
import com.recover.photo.ui.onboarding.adapter.OnboardingPagerAdapter
import com.recover.photo.utils.SharedPrefHelper

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

