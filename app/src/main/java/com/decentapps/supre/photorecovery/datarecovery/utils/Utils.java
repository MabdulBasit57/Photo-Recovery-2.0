package com.decentapps.supre.photorecovery.datarecovery.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.LoadAdError;
//import com.google.android.gms.ads.interstitial.InterstitialAd;
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.decentapps.supre.photorecovery.datarecovery.R;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Utils {
//    public static InterstitialAd mInterstitialAd;
    public static Boolean isShowingAd=false;
    public static Boolean isAdAlreadyOpen=false;
    public static String formatSize(long j) {
        if (j <= 0) {
            return "";
        }
        double d = (double) j;
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        StringBuilder sb = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double pow = Math.pow(1024.0d, (double) log10);
        Double.isNaN(d);
        sb.append(decimalFormat.format(d / pow));
        sb.append(" ");
        sb.append(new String[]{"B", "KB", "MB", "GB", "TB"}[log10]);
        return sb.toString();
    }
    public static void loadInters(Context context) {
       /* if(mInterstitialAd==null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, context.getString(R.string.home_interstitial_id), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                        }
                    });
        }*/
    }
/*    public static void showBannerAd(AdView mAdView) {
        try {
          *//*  mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);*//*
        }
        catch (Exception e){
           //
        }

    }*/
    public static String getFileName(String str) {
        return str.substring(str.lastIndexOf("/") + 1);
    }

    public static File[] getFileList(String str) {
        PrintStream printStream = System.out;
        printStream.println("str     " + str);
        File file = new File(str);
        if (!file.isDirectory()) {
            return new File[0];
        }
        return file.listFiles();
    }

    public static boolean checkSelfPermission(Activity activity, String str) {
        if (!isAndroid23() || ContextCompat.checkSelfPermission(activity, str) == 0) {
            return true;
        }
        return false;
    }

    public static boolean isAndroid23() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static String getFileTitle(String str) {
        return str.substring(str.lastIndexOf("/") + 1);
    }

    public static String getPathSave(Context context, String str) {

        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + str;
    }

    public static String convertDuration(long j) {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }
}
