package com.recover.photo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.google.android.gms.ads.AdView;
import com.recover.photo.pj.VideoModel;
import com.recover.photo.R;
import com.recover.photo.utils.LoadingAnimation;
import com.recover.photo.utils.Utils;
import com.recover.photo.tasks.RecoverOneVideosAsyncTask;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;

public class FileInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private LoadingAnimation LAC;
    Button btnOpen;
    Button btnRestore;
    Button btnShare;
    ImageView ivVideo;
    RecoverOneVideosAsyncTask mRecoverOneVideosAsyncTask;
    VideoModel mVideoModel;
    private RelativeLayout rl;
    SharedPreferences sharedPreferences;

    TextView tvDate;
    TextView tvSize;
    TextView tvType;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_file_info);
        intView();
        intData();
        intEvent();
       /* AdView mAdView = findViewById(R.id.madView);
        Utils.showBannerAd(mAdView);*/
    }

    public void intView() {
       setTitle(getString(R.string.restore_photo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.btnOpen = findViewById(R.id.btnOpen);
        this.btnShare = findViewById(R.id.btnShare);
        this.btnRestore = findViewById(R.id.btnRestore);
        this.tvDate = findViewById(R.id.tvDate);
        this.tvSize = findViewById(R.id.tvSize);
        this.tvType = findViewById(R.id.tvType);
        this.ivVideo = findViewById(R.id.ivVideo);
        this.rl = findViewById(R.id.activity_file_info_cont);
    }

    @SuppressLint("SetTextI18n")
    public void intData() {
        this.mVideoModel = (VideoModel) getIntent().getSerializableExtra("ojectVideo");
        TextView textView = this.tvDate;
        textView.setText(DateFormat.getDateInstance().format(this.mVideoModel.getLastModified()) + "  " + this.mVideoModel.timeDuration);
        this.tvSize.setText(Utils.formatSize(this.mVideoModel.sizePhoto));
        this.tvType.setText(this.mVideoModel.typeFile);
        RequestManager with = Glide.with(this);
        ((RequestBuilder) ((RequestBuilder) with.load("file://" + this.mVideoModel.pathPhoto).diskCacheStrategy(DiskCacheStrategy.ALL)).
                priority(Priority.HIGH).centerCrop().error((int) R.drawable.ic_launcher)).into(this.ivVideo);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void intEvent() {
        this.btnOpen.setOnClickListener(this);
        this.btnShare.setOnClickListener(this);
        this.btnRestore.setOnClickListener(this);
        this.ivVideo.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOpen:
                openFile(this.mVideoModel.pathPhoto);
                return;
            case R.id.btnRestore:
                ArrayList arrayList = new ArrayList();
                arrayList.add("X");
                mRecoverOneVideosAsyncTask = new RecoverOneVideosAsyncTask(FileInfoActivity.this, mVideoModel, new RecoverOneVideosAsyncTask.OnRestoreListener() {
                    public void onComplete(String str) {
                        if (str.isEmpty()) {
                            Intent intent = new Intent(FileInfoActivity.this.getApplicationContext(), RestoreResultActivity.class);
                            intent.putExtra("value", 1);
                            FileInfoActivity.this.startActivity(intent);
                            FileInfoActivity.this.finish();
                        } else if (str.equals("Er1")) {
                            FileInfoActivity fileInfoActivity = FileInfoActivity.this;
                            Toast.makeText(fileInfoActivity, fileInfoActivity.getString(R.string.FileDeletedBeforeScan), Toast.LENGTH_LONG).show();
                            Intent intent2 = new Intent(FileInfoActivity.this.getApplicationContext(), MainActivity.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            FileInfoActivity.this.startActivity(intent2);
                        }
                    }
                });

                mRecoverOneVideosAsyncTask.execute(new String[0]);
                return;
            case R.id.btnShare:
                shareVideo(this.mVideoModel.pathPhoto);
                return;
            case R.id.ivVideo:
                openFile(this.mVideoModel.pathPhoto);
                return;
            default:
                return;
        }

    }

    public void cancleUIUPdate() {
        RecoverOneVideosAsyncTask recoverOneVideosAsyncTask = this.mRecoverOneVideosAsyncTask;
        if (recoverOneVideosAsyncTask != null && recoverOneVideosAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            this.mRecoverOneVideosAsyncTask.cancel(true);
            this.mRecoverOneVideosAsyncTask = null;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public boolean SDCardCheck() {
        File[] externalFilesDirs = ContextCompat.getExternalFilesDirs(this, (String) null);
        return externalFilesDirs.length > 1 && externalFilesDirs[0] != null && externalFilesDirs[1] != null;
    }

    public void fileSearch() {
        startActivityForResult(new Intent("android.intent.action.OPEN_DOCUMENT_TREE"), 100);
    }

    public void openFile(String str) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 24) {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setDataAndType(Uri.fromFile(new File(str)), "video/*");
            intent = Intent.createChooser(intent2, "Complete action using");
        } else {
            File file = new File(str);
            Intent intent3 = new Intent("android.intent.action.VIEW");
            try {
                Uri uriForFile = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
                grantUriPermission(getPackageName(), uriForFile, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent3.setType("*/*");
                if (Build.VERSION.SDK_INT < 24) {
                    uriForFile = Uri.fromFile(file);
                }
                intent3.setData(uriForFile);
                intent3.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent = Intent.createChooser(intent3, "Complete action using");
            } catch (Exception unused) {
                return;
            }
        }
        startActivity(intent);
    }

    private void shareVideo(String str) {
        try {
            startActivity(Intent.createChooser(new Intent().setAction("android.intent.action.SEND").setType("video/*").setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(str))), ""));
        } catch (Exception unused) {
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        cancleUIUPdate();
    }

    private static boolean checkIfSDCardRoot(Uri uri) {
        return isExternalStorageDocument(uri) && isRootUri(uri) && !isInternalStorage(uri);
    }

    private static boolean isRootUri(Uri uri) {
        return DocumentsContract.getTreeDocumentId(uri).endsWith(":");
    }

    public static boolean isInternalStorage(Uri uri) {
        return isExternalStorageDocument(uri) && DocumentsContract.getTreeDocumentId(uri).contains("primary");
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public void onActivityResult(int r10, int r11, Intent r12) {
        super.onActivityResult(r10, r11, r12);
        throw new UnsupportedOperationException("Method not decompiled: com.narmx.photosrecovery.model.modul.recoveryvideo.FileInfoActivity.onActivityResult(int, int, android.content.Intent):void");
    }
}
