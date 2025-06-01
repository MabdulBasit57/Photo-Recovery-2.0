package com.recover.photo.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.recover.photo.R
import com.recover.photo.ui.onboarding.fragments.BottomAdFragment
import com.recover.photo.ui.onboarding.fragments.FullScreenAdFragment

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BottomAdFragment.newInstance("Title 1", "Desc 1", R.drawable.home_bg)
            1 -> FullScreenAdFragment.newInstance("Title 2", "Desc 2", R.drawable.home_bg)
            2 -> BottomAdFragment.newInstance("Title 3", "Desc 3", R.drawable.home_bg)
            3 -> FullScreenAdFragment.newInstance("Title 4", "Desc 4", R.drawable.home_bg)
            4 -> BottomAdFragment.newInstance("Title 5", "Desc 5", R.drawable.home_bg)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
