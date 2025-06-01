package com.recover.photo.ui.onboarding

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.ump.ConsentInformation
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.recover.photo.R
import com.recover.photo.databinding.ActivityMainBinding
import com.recover.photo.databinding.ActivityOnboardingBinding
import com.recover.photo.ui.activity.HomeActivity
import com.recover.photo.ui.onboarding.adapter.OnboardingAdapter
import com.recover.photo.ui.onboarding.adapter.OnboardingItem
import com.recover.photo.utils.AppUtils.filter
import com.recover.photo.utils.SharedPrefHelper
import com.recover.photo.utils.Utils
import com.recover.photo.utils.percentage
import com.recover.photo.utils.storageValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.log10
import kotlin.math.pow

class OnboardingActivity : AppCompatActivity(){
    private val binding by lazy { ActivityOnboardingBinding.inflate(layoutInflater) }
    private val onboardingItems = listOf(
        OnboardingItem("Title 1", "Description 1", R.drawable.home_bg),
        OnboardingItem("Title 2", "Description 2", R.drawable.home_bg),
        OnboardingItem("Title 3", "Description 3", R.drawable.home_bg),
        OnboardingItem("Title 4", "Description 4", R.drawable.home_bg),
        OnboardingItem("Title 5", "Description 5", R.drawable.home_bg)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        binding.btnSkip.setOnClickListener {
            SharedPrefHelper.setNotFirstTime(this)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem + 1 < onboardingItems.size) {
                binding.viewPager.currentItem += 1
            } else {
                SharedPrefHelper.setNotFirstTime(this)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}