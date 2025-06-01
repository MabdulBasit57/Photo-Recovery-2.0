package com.recover.photo.ui.onboarding.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.recover.photo.R

class BottomAdFragment : Fragment() {

    companion object {
        fun newInstance(title: String, desc: String, image: Int): BottomAdFragment {
            val fragment = BottomAdFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("desc", desc)
            args.putInt("image", image)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_bottom_ad, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = arguments?.getString("title")
        val desc = arguments?.getString("desc")
        val image = arguments?.getInt("image") ?: 0

        view.findViewById<TextView>(R.id.titleView).text = title
        view.findViewById<TextView>(R.id.descView).text = desc
        view.findViewById<ImageView>(R.id.imageView).setImageResource(image)

        // TODO: Load native ad into adContainer
    }
}
