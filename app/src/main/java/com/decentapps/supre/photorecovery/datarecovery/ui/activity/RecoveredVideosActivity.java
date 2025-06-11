package com.decentapps.supre.photorecovery.datarecovery.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.google.android.gms.ads.AdView;
import com.decentapps.supre.photorecovery.datarecovery.adapter.MainListener;
import com.decentapps.supre.photorecovery.datarecovery.adapter.RecoveredFilesAdapter;
import com.decentapps.supre.photorecovery.datarecovery.R;
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecoveredVideosActivity extends AppCompatActivity implements MainListener {

    RecyclerView filesRecyclerView;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovered_images);

        setTitle("Recovered Videos");
        relativeLayout = findViewById(R.id.deleteLay);

        filesRecyclerView = findViewById(R.id.filesRecyclerView);
        filesRecyclerView.setHasFixedSize(true);
        filesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        filesRecyclerView.setAdapter(new RecoveredFilesAdapter(this, getAllFiles(), this));
    }

    public List<File> getAllFiles() {
        List<File> files = new ArrayList<>();
        File f = new File(Utils.getPathSave(this, getString(R.string.restore_folder_path_video)));

        File[] FileList = f.listFiles();

        if (FileList == null) {
            relativeLayout.setVisibility(View.VISIBLE);
      /*      AdView mAdView = findViewById(R.id.madView);
            mAdView.setVisibility(View.GONE);*/
            Toast.makeText(this, "No Videos Recovered Yet", Toast.LENGTH_SHORT).show();
        } else {

            relativeLayout = findViewById(R.id.deleteLay);
            relativeLayout.setVisibility(View.GONE);

            for (File file : FileList) {

                if (file.getName().endsWith(".mp4") || file.getName().endsWith(".mkv")) {
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
        Intent tostart = new Intent(Intent.ACTION_VIEW);
        Uri a = FileProvider.getUriForFile(this, getPackageName() + ".provider", getAllFiles().get(position));
        tostart.setDataAndType(a, "video/*");
        tostart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(tostart);
    }
}