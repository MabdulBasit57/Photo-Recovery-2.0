package com.recover.photo.ui.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.recover.photo.R
import com.recover.photo.adapter.AdapterRestoredImages
import com.recover.photo.async.ScanImagesAsyncTask
import com.recover.photo.pj.ImageData
import com.recover.photo.utils.Config
import java.io.File

class RestoredScannerActivity : AppCompatActivity() {
    // InterstitialAd mInterstitialAd;
    private var ivLoading: ImageView? = null
    private var adapterImage: AdapterRestoredImages? = null
    private val alImageData = ArrayList<ImageData>()
    private var tvScannedPics: TextView? = null

    //faceBok Ads
    var TAG: String = "MainActz"

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_scanner)
        init()
    } //////////////

    fun init() {
        val myDataHandler: MyDataHandler = MyDataHandler()
        val gvAllPics = findViewById<GridView>(R.id.gvGallery)
        adapterImage = AdapterRestoredImages(this, this.alImageData)
        gvAllPics.adapter = adapterImage
        tvScannedPics = findViewById(R.id.tvItems)
        ivLoading = findViewById(R.id.iv_start_scan_anim)
        ivLoading?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scan_animation))
        ScanImagesAsyncTask(this, myDataHandler).execute("restored")
    }


    //ADMOB
    /* void initAds() {


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("Admob", "onAdLoaded");
                        showAds();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("Admob", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }


    void showAds()
    {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(RestoredScannerActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }

    }*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_delete) {
            val builder = AlertDialog.Builder(this@RestoredScannerActivity)
            builder.setMessage("Delete selected items?")
            builder.setNegativeButton("No") { dialog: DialogInterface?, which: Int -> }
            builder.setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                deletePictures(
                    adapterImage!!.selectedItem
                )
            }
            builder.create()
            builder.show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun deletePictures(arrayList: ArrayList<ImageData>?) {
        for (i in arrayList!!.indices) {
            val delFile = File(arrayList[i].filePath)
            if (delFile.delete()) {
                this@RestoredScannerActivity.sendBroadcast(
                    Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(delFile)
                    )
                )
            }
        }

        val intent = intent
        finish()
        startActivity(intent)
    }

    fun callBroadCast() {
        Log.e("-->", " >= 14")
        MediaScannerConnection.scanFile(
            this,
            arrayOf(
                Environment.getExternalStorageDirectory().toString() + "/RestoredPhotos".toString()
            ),
            null
        ) { path, uri ->
            Log.e("ExternalStorage", "Scanned $path:")
            Log.e("ExternalStorage", "-> uri=$uri")
        }
    }

    @SuppressLint("HandlerLeak")
    inner class MyDataHandler : Handler() {
        internal inner class AnimationHandlerClass : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {
            }

            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                ivLoading!!.visibility = View.GONE
            }
        }

        override fun handleMessage(message: Message) {
            super.handleMessage(message)
            if (message.what == Config.DATA) {
                val animation: Animation = AlphaAnimation(1.0f, 0.0f)
                animation.duration = 500
                animation.fillAfter = true
                animation.setAnimationListener(AnimationHandlerClass())
                alImageData.clear()
                alImageData.addAll(message.obj as ArrayList<ImageData>)
                adapterImage?.notifyDataSetChanged()
                tvScannedPics?.visibility = View.GONE
                ivLoading?.clearAnimation()
                ivLoading?.startAnimation(animation)

                // initAds();
            } else if (message.what == Config.REPAIR) {
                adapterImage?.notifyDataSetChanged()
            } else if (message.what == Config.UPDATE) {
                val tvFoundItems = tvScannedPics
                val sbProgressMessage = message.obj.toString() +
                        " " +
                        "files found"
                tvFoundItems?.text = sbProgressMessage
            }
        }
    }
}