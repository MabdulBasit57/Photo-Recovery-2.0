package com.recover.photo.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.recover.photo.ui.fragment.AudioFragment
import com.recover.photo.ui.fragment.ImageFragment
import com.recover.photo.ui.fragment.VideoFragment

class RecoveredPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> VideoFragment()
            1 -> ImageFragment()
            2 -> AudioFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}