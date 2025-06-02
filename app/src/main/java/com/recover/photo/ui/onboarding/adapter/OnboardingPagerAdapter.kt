package com.recover.photo.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.recover.photo.ui.onboarding.fragments.BottomAdFragment
import com.recover.photo.ui.onboarding.fragments.BottomAdFragment2
import com.recover.photo.ui.onboarding.fragments.BottomAdFragment3
import com.recover.photo.ui.onboarding.fragments.FullScreenAdFragment
import com.recover.photo.ui.onboarding.fragments.FullScreenAdFragment2

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BottomAdFragment()
            1 -> FullScreenAdFragment()
            2 -> BottomAdFragment2()
            3 -> FullScreenAdFragment2()
            4 -> BottomAdFragment3()
            else -> BottomAdFragment()
        }
    }
}
