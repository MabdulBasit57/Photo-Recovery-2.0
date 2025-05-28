package com.recover.photo.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.recover.photo.R
import com.recover.photo.ui.adapter.RecoveredPagerAdapter

class RecoveredFilesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovered_files)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val titleToolbar = findViewById<TextView>(R.id.titleToolbar)
        val backToolbar = findViewById<ImageView>(R.id.backToolbar)
        backToolbar.setOnClickListener {
            onBackPressed()
        }
        titleToolbar.text="Recovered Data"
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        try {

// Set custom view for each tab
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            tabLayout.tabGravity = TabLayout.GRAVITY_CENTER
            tabLayout.post {
                val tabStrip = tabLayout.getChildAt(0) as ViewGroup
                for (i in 0 until tabStrip.childCount) {
                    val tabView = tabStrip.getChildAt(i) as ViewGroup

                    // Set margins
                    val lp = tabView.layoutParams as ViewGroup.MarginLayoutParams
                    lp.setMargins(8, 8, 8, 8)
                    tabView.layoutParams = lp

                    // Set padding and min width
                    tabView.setPadding(45, 4, 45, 4)
                    tabView.minimumWidth = 70 // override default large minWidth

                    // Change the text appearance
                    for (j in 0 until tabView.childCount) {
                        val tabChild = tabView.getChildAt(j)
                        if (tabChild is TextView) {
                            tabChild.isAllCaps = false
                            tabChild.textSize = 13f
                        }
                    }
                }
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }
        val adapter = RecoveredPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Videos"
                1 -> "Images"
                2 -> "Audios"
                else -> null
            }
        }.attach()
    }
}