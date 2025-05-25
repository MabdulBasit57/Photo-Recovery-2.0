package com.recover.photo.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.recover.photo.R
import com.recover.photo.databinding.ActivityPhotosBinding
import com.recover.photo.databinding.ActivityViewRecoveredImagesBinding
import java.io.File
import java.util.Objects

class ViewRecoveredImages : AppCompatActivity() {
    var files: ArrayList<File>? = ArrayList()
    private val binding by lazy { ActivityViewRecoveredImagesBinding.inflate(layoutInflater) }

    var pos:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.customToolbar.backToolbar.setOnClickListener {
            onBackPressed()
        }
        binding.customToolbar.titleToolbar.text="Recovered Image"
        files?.clear()
        files = intent.getSerializableExtra("files") as ArrayList<File>?
        pos = intent.getIntExtra("position",0)
        val mViewPager = findViewById<ViewPager>(R.id.viewPagerMain)
        val mViewPagerAdapter = ViewPagerAdapter(this, files)
        mViewPager.adapter = mViewPagerAdapter
        mViewPager.currentItem = pos
    }

    internal inner class ViewPagerAdapter(var context: Context, var images: ArrayList<File>?) :
        PagerAdapter() {
        var mLayoutInflater: LayoutInflater =
            context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return images?.size?:0
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === (`object` as LinearLayout)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = mLayoutInflater.inflate(R.layout.imgs_layout, container, false)
            val imageView = itemView.findViewById<View>(R.id.imageViewMain) as ImageView
            Glide.with(context).load(images!![position]).into(imageView)
            Objects.requireNonNull(container).addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as LinearLayout)
        }
    }
}