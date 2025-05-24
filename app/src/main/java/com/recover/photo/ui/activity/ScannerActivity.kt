package com.recover.photo.ui.activity

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.recover.photo.R
import com.recover.photo.adapter.AdapterImage
import com.recover.photo.async.RecoverPhotosAsyncTask
import com.recover.photo.async.ScanImagesAsyncTask
import com.recover.photo.pj.ImageData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

class ScannerActivity : AppCompatActivity() {
    // InterstitialAd mInterstitialAd;
    private var ivLoading: ImageView? = null
    private var backToolbar: ImageView? = null
    private var adapterImage: AdapterImage? = null
    private var myDataHandler: MyDataHandler? = null
    private var gvAllPics: GridView? = null
    private val alImageData: ArrayList<ImageData>?=null
    private var tvScannedPics: TextView? = null
    private var titleToolbar: TextView? = null

    //faceBook Ads
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_scanner)
        init()
    }

    fun init() {
        backToolbar = findViewById(R.id.backToolbar)
        titleToolbar = findViewById(R.id.titleToolbar)
        backToolbar?.setOnClickListener {
            onBackPressed()
        }
        titleToolbar?.text="Scanner Activity"
        myDataHandler = MyDataHandler()
        gvAllPics = findViewById(R.id.gvGallery)
        adapterImage = AdapterImage(this, this.alImageData)
        gvAllPics?.adapter = this.adapterImage
        tvScannedPics = findViewById(R.id.tvItems)
        ivLoading = findViewById(R.id.iv_start_scan_anim)
        ivLoading?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scan_animation))
        ScanImagesAsyncTask(this, this.myDataHandler).execute("all")

        //        showBannerAd();
    }


    //Admob
    /*void initAds() {


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
            mInterstitialAd.show(ScannerActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }

    }
*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.restore_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_restore) {
            repairingPictures(adapterImage?.selectedItem)
            //            ArrayList<ImageData> alSelectedItems = adapterImage.getSelectedItem();
//
//            for (int i=0;i<alSelectedItems.size();i++){
//                copy(alSelectedItems.get(i).getFilePath());
//            }
//
//            Toast.makeText(this, "Restored", Toast.LENGTH_SHORT).show();
//            toolbar.setVisibility(View.GONE);
//            adapterImage.setAllImagesUnseleted();
//            adapterImage.notifyDataSetChanged();
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun repairingPictures(arrayList: ArrayList<ImageData>?) {
//        new RecoverPhotosAsyncTask(this, arrayList, this.myDataHandler).execute(new String[0]);
        val repairTask = arrayList?.let { RecoverPhotosAsyncTask(this, it, myDataHandler) }
        repairTask?.execute()
    }


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

        //        @Override
        override fun handleMessage(message: Message) {
            super.handleMessage(message)
            if (message.what == 1000) {
                val animation: Animation = AlphaAnimation(1.0f, 0.0f)
                animation.duration = 500
                animation.fillAfter = true
                animation.setAnimationListener(AnimationHandlerClass())
                alImageData?.clear()
                alImageData?.addAll(message.obj as ArrayList<ImageData>)
                adapterImage!!.notifyDataSetChanged()
                tvScannedPics!!.visibility = View.GONE
                ivLoading!!.clearAnimation()
                ivLoading!!.startAnimation(animation)

                //  initAds();
            } else if (message.what == 2000) {
                adapterImage!!.setAllImagesUnseleted()
                adapterImage!!.notifyDataSetChanged()
            } else if (message.what == 3000) {
                val tvFoundItems = tvScannedPics
                val sbProgressMessage = StringBuilder()
                sbProgressMessage.append(message.obj.toString())
                sbProgressMessage.append(" ")
                sbProgressMessage.append("files found")
                tvFoundItems!!.text = sbProgressMessage.toString()
            }
        }
    }


    fun copy(sourceFilePath: String?) {
        val sourceFile = File(sourceFilePath)
        val destinationFile =
            File(Environment.getExternalStorageDirectory().absolutePath + "/RestoredPhotos/" + System.currentTimeMillis() + ".png")
        if (sourceFile.exists()) {
            try {
                copyFile(sourceFile, destinationFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var toolbar: Toolbar? = null

        @Throws(IOException::class)
        fun copyFile(sourceFile: File?, destFile: File) {
            if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()

            if (!destFile.exists()) {
                destFile.createNewFile()
            }

            var source: FileChannel? = null
            var destination: FileChannel? = null

            try {
                source = FileInputStream(sourceFile).channel
                destination = FileOutputStream(destFile).channel
                destination.transferFrom(source, 0, source.size())
            } finally {
                source?.close()
                destination?.close()
            }
        }
    }
} //    private void init() {
//        lv = findViewById(R.id.gvGallery);
//        alAllFiles = new ArrayList<>();
//        btnOk = findViewById(R.id.btnOk);
//        tvItems = findViewById(R.id.tvItems);
//
//        String strArr = Environment.getExternalStorageDirectory().getAbsolutePath();
//
//        checkFileOfDirectory(getFileList(strArr));
//
//
////        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,alAllFiles);
//
////        AdapterImage adapterImage = new AdapterImage(ScannerActivity.this,alAllFiles);
////        Toast.makeText(ScannerActivity.this, ""+alAllFiles.size(), Toast.LENGTH_LONG).show();
////        lv.setAdapter(adapterImage);
//
//    }
//    private void listeners() {
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ScannerActivity.this);
//                String strData="";
//                for (int i = 0 ; i<alSelected.size() ; i++){
//                    strData+=alSelected.get(i)+"\n";
//                }
//                builder.setMessage(strData);
//                builder.create();
//                builder.show();
//            }
//        });
//
//    }
//    public File[] getFileList(String str) {
//        File file = new File(str);
//        if (!file.isDirectory()) {   // check with !
//            return null;
//        }
//        return file.listFiles();
//    }
//    public void checkFileOfDirectory(File[] fileArr) {
//        for (int i = 0; i < fileArr.length; i++) {
//            if (fileArr[i].isDirectory()) {
//                checkFileOfDirectory(getFileList(fileArr[i].getFilePath()));
//            } else {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//
//                fileName = fileArr[i].getFilePath();
//                if (fileName.endsWith(".exo") || fileName.endsWith(".mp3") || fileName.endsWith(".mp4") || fileName.endsWith(".pdf") || fileName.endsWith(".apk")
//                        || fileName.endsWith(".txt") || fileName.endsWith(".doc") || fileName.endsWith(".exi") || fileName.endsWith(".dat")
//                        || fileName.endsWith(".m4a") || fileName.endsWith(".json") || fileName.endsWith(".chck")) {
//                    //do nothing, just skip these files
//                    //
//                } else if (fileName.endsWith(".0")){
//                    alAllFiles.add(fileArr[i].getFilePath());
//                    items++;
//                    Toast.makeText(ScannerActivity.this, ""+items, Toast.LENGTH_SHORT).show();
//                    tvItems.setText(items+" items scanned");
//                }
//                BitmapFactory.decodeFile(fileArr[i].getFilePath(), options);
//            }
//        }
//    }

