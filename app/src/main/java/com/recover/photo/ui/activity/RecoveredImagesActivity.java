package com.recover.photo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.google.android.gms.ads.AdView;
import com.recover.photo.R;
import com.recover.photo.utils.Utils;
import com.recover.photo.adapter.MainListener;
import com.recover.photo.adapter.RecoveredFilesAdapter;

import java.io.File;
import java.util.ArrayList;

public class RecoveredImagesActivity extends AppCompatActivity implements MainListener {

    RecyclerView filesRecyclerView;
    ArrayList<File> files = new ArrayList<>();
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovered_images);

        setTitle("Recovered Images");
        filesRecyclerView = findViewById(R.id.filesRecyclerView);
        relativeLayout = findViewById(R.id.deleteLay);
        filesRecyclerView.setHasFixedSize(true);
        files = getAllFiles();

        filesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        filesRecyclerView.setAdapter(new RecoveredFilesAdapter(this, files, this));
    }


    public ArrayList<File> getAllFiles() {
        ArrayList<File> files = new ArrayList<>();

//        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME};
//        String selection = MediaStore.Images.Media.DATA + " is not null";
//
//        try (Cursor cursor = getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                projection,
//                selection,
//                null,
//                null
//        )) {
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    long imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//                    Uri imageContentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.toString(imageId));
//
//                    files.add(new File(String.valueOf(imageContentUri)));
//                }
//
//            }
//        }



        File f = new File(Utils.getPathSave(this, "Recovered Photos"));

        File[] FileList = f.listFiles();

        if (FileList == null) {
          /*  AdView mAdView = findViewById(R.id.madView);
            mAdView.setVisibility(View.GONE);*/
            Toast.makeText(this, "No Images Recovered Yet", Toast.LENGTH_SHORT).show();
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout = findViewById(R.id.deleteLay);
            relativeLayout.setVisibility(View.GONE);
            for (File file : FileList) {
                if (file.getName().endsWith(".png") ||
                        file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".gif")) {
                    files.add(file);
                }
            }
          /*  if(!files.isEmpty()){
                AdView mAdView = findViewById(R.id.madView);
                Utils.showBannerAd(mAdView);
            }*/
        }
        return files;
    }

    @Override
    public void begin(int position) {
        Intent intent = new Intent(this, ViewRecoveredImages.class);
        intent.putExtra("position", position);
        intent.putExtra("files", files);
        startActivity(intent);
    }
}