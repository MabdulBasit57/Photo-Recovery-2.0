package com.recover.photo.ui.onboarding.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.recover.photo.R
import com.recover.photo.databinding.ActivityMainBinding
import com.recover.photo.databinding.FragmentBottomAd3Binding
import com.recover.photo.ui.onboarding.OnboardingActivity

class BottomAdFragment3 : Fragment() {
    var mActivity:FragmentActivity?=null
    private val binding by lazy { FragmentBottomAd3Binding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mActivity?.let {
            binding.next.setOnClickListener {
                (activity as? OnboardingActivity)?.navigateNext()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mActivity=null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity=requireActivity()
    }
}
