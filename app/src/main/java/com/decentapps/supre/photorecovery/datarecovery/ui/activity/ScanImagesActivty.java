package com.decentapps.supre.photorecovery.datarecovery.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.decentapps.supre.photorecovery.datarecovery.pj.AlbumAudio;
import com.decentapps.supre.photorecovery.datarecovery.pj.AlbumPhoto;
import com.decentapps.supre.photorecovery.datarecovery.pj.AlbumVideo;
import com.decentapps.supre.photorecovery.datarecovery.pj.AudioModel;
import com.decentapps.supre.photorecovery.datarecovery.pj.PhotoModel;
import com.decentapps.supre.photorecovery.datarecovery.pj.VideoModel;
import com.decentapps.supre.photorecovery.datarecovery.R;
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScanImagesActivty extends AppCompatActivity {

    TextView tvNumber;
    TextView tvPath;
    ImageView backToolbar;

    TextView title;
    public static ArrayList<AlbumAudio> mAlbumAudio = new ArrayList<>();
    public static ArrayList<AlbumPhoto> mAlbumPhoto = new ArrayList<>();
    public static ArrayList<AlbumVideo> mAlbumVideo = new ArrayList<>();
    public static int itemSize = 0;
    int position = -1;
    ScanAsyncTask mScanAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_images_activty);
        position = getIntent().getIntExtra("position", -1);
        tvNumber = findViewById(R.id.tvNumber);
        tvPath = findViewById(R.id.tvPath);
        title = findViewById(R.id.titleToolbar);
        backToolbar = findViewById(R.id.backToolbar);
        backToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (position == 0) {
            title.setText("Scanning Photos");
            scanType(0);
        } else if (position == 1) {
            title.setText("Scanning Videos");
            scanType(1);
        } else {
            title.setText("Scanning Audios");
            scanType(2);
        }
    }
    @Override
    public void onBackPressed() {
        if (mScanAsyncTask != null && mScanAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            // If the AsyncTask is running, cancel it
            mScanAsyncTask.cancel(true);
            finish();
        } else {
            // If there's no ongoing scanning task, proceed with the regular back press behavior
            super.onBackPressed();
        }
    }


    public void scanType(int i) {
        ScanAsyncTask scanAsyncTask = this.mScanAsyncTask;

        if (scanAsyncTask == null || scanAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            mAlbumAudio.clear();
            mAlbumPhoto.clear();
            mAlbumVideo.clear();
            this.tvNumber.setVisibility(View.VISIBLE);
            this.tvNumber.setText(getString(R.string.analyzing));
            this.mScanAsyncTask = new ScanAsyncTask(i);
        this.mScanAsyncTask.setFilterDays(com.decentapps.supre.photorecovery.datarecovery.utils.AppUtils.INSTANCE.getFilter());
            this.mScanAsyncTask.execute(new Void[i]);

            return;
        }
        Toast.makeText(this, getString(R.string.scan_wait), Toast.LENGTH_LONG).show();
    }

    public class ScanAsyncTask extends AsyncTask<Void, Integer, Void> {
        public int filterDays = 0;
        ArrayList<AudioModel> listAudio = new ArrayList<>();
        ArrayList<PhotoModel> listPhoto = new ArrayList<>();
        ArrayList<VideoModel> listVideo = new ArrayList<>();
        int number = 0;
        int typeScan = 0;
        public void setFilterDays(int days) {
            this.filterDays = days;
        }
        public ScanAsyncTask(int i) {
            this.typeScan = i;
        }

        public void onPreExecute() {
            super.onPreExecute();
            this.number = 0;
        }

        private void nativeAddHide(){
//            if (nativeAd != null) {
//                nativeAd.destroy();
//            }

        }

        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            tvNumber.setText("");
            tvNumber.setVisibility(View.INVISIBLE);
            if (this.typeScan == 0) {
                if (mAlbumPhoto.size() == 0) {
                    startActivity(new Intent(ScanImagesActivty.this, NoFileActiviy.class));

                } else {
                    startActivity(new Intent(ScanImagesActivty.this, AlbumPhotoActivity.class));
                    nativeAddHide();
                }
                finish();
            }
            if (this.typeScan == 1) {
                if (mAlbumVideo.size() == 0) {
                    startActivity(new Intent(ScanImagesActivty.this, NoFileActiviy.class));

                } else {
                    startActivity(new Intent(ScanImagesActivty.this, AlbumVideoActivity.class));
                    nativeAddHide();
                }
                finish();
            }
            if (this.typeScan != 2) {
                return;
            }
            if (mAlbumAudio.size() == 0) {
                startActivity(new Intent(ScanImagesActivty.this, NoFileActiviy.class));
                finish();
                return;
            }
            startActivity(new Intent(ScanImagesActivty.this, AlbumAudioActivity.class));
            nativeAddHide();
            finish();
        }


        @SuppressLint("SetTextI18n")
        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
            tvNumber.setText("Scanned Files\n" + numArr[0]);
            itemSize=numArr[0];
        }

        public Void doInBackground(Void... voidArr) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            File file = new File(absolutePath);
            absolutePath = file.getAbsoluteFile().getParent();

            Log.d("absolutePath =", absolutePath);

            // Set your filter days: 0 = no filter, 7 = 7 days, 30 = 1 month
            int filterDays = this.filterDays;
            long filterMillis = filterDays * 24L * 60 * 60 * 1000;
            long currentTime = System.currentTimeMillis();

            if (this.typeScan == 0) { // Scanning Images
                try {
                    getSdCardImage();

                    // ✅ Custom lightweight file list (if needed)
                    File[] files = Utils.getFileList(absolutePath);
                    if (files != null && files.length > 0) {
                        checkFileOfDirectoryImageFiltered(absolutePath, files, filterMillis, currentTime);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // ✅ Sort scanned albums by folder last modified time
                Collections.sort(mAlbumPhoto, (a, b) -> Long.compare(b.lastModified, a.lastModified));
            }
            if (this.typeScan == 1) {
                getSdCardVideo(filterDays);
                checkFileOfDirectoryVideo(absolutePath, Utils.getFileList(absolutePath),filterDays);
                Collections.sort(mAlbumVideo, new Comparator<AlbumVideo>() {
                    public int compare(AlbumVideo albumVideo, AlbumVideo albumVideo2) {
                        return Long.compare(albumVideo2.lastModified, albumVideo.lastModified);
                    }
                });
            }
            if (this.typeScan == 2) {
                try {
                    getSdCardAudio(filterDays);
                    checkFileOfDirectoryAudio(absolutePath, Utils.getFileList(absolutePath),filterDays);
                } catch (Exception unused2) {
                    unused2.printStackTrace();
                }
                Collections.sort(mAlbumAudio, (albumAudio, albumAudio2) -> Long.compare(albumAudio2.lastModified, albumAudio.lastModified));
            }
            try {
                Thread.sleep(3000);
                return null;
            } catch (InterruptedException e2) {
                e2.printStackTrace();
                return null;
            }
        }
        public void checkFileOfDirectoryImageFiltered(String str, File[] fileArr, long filterMillis, long currentTime) {
            boolean contains = str.contains(Utils.getPathSave(ScanImagesActivty.this, getString(R.string.restore_folder_path_photo)));
            int i2 = 0;
            boolean z = false;

            while (i2 < fileArr.length) {
                File file = fileArr[i2];
                if (file.isDirectory()) {
                    String path = file.getPath();
//                    tvPath.setText(file.getPath().toString());
                    // ✅ Skip hidden/system folders
                  /*  if (file.isHidden() || path.contains("/Android/") || path.contains("/cache/")) {
                        i2++;
                        continue;
                    }*/

                    File[] fileList = Utils.getFileList(path);
                    if (fileList != null && fileList.length > 0) {
                        checkFileOfDirectoryImageFiltered(path, fileList, filterMillis, currentTime);
                    }

                } else {
                    long lastModified = file.lastModified();

                    // ✅ Skip old files
                    if (filterMillis > 0 && (currentTime - lastModified) > filterMillis) {
                        i2++;
                        continue;
                    }

                    // ✅ Check image metadata only if date matches
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = 8;
                    options.inDither = z;
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inTempStorage = new byte[32768];
                    BitmapFactory.decodeFile(file.getPath(), options);

                    if (!(contains || options.outWidth == -1 || options.outHeight == -1)) {
                        if (isImageFile(file)) {
                            long fileSize = file.length();
                            listPhoto.add(new PhotoModel(file.getPath(), lastModified, fileSize));
                            number++;
                            publishProgress(number);
                        }
                    }
                }

                i2++;
            }

            // ✅ Save scanned album if photos found
            if (!listPhoto.isEmpty()) {
                AlbumPhoto albumPhoto = new AlbumPhoto();
                albumPhoto.str_folder = str;
                albumPhoto.lastModified = new File(str).lastModified();

                Collections.sort(listPhoto, (a, b) -> Long.compare(b.getLastModified(), a.getLastModified()));
                albumPhoto.listPhoto = (ArrayList<PhotoModel>) listPhoto.clone();
                mAlbumPhoto.add(albumPhoto);
            }

            listPhoto.clear();
        }

        public void checkFileOfDirectoryImage(String str, File[] fileArr) {
            int i;
            boolean contains = str.contains(Utils.getPathSave(ScanImagesActivty.this, getString(R.string.restore_folder_path_photo)));
            boolean z = false;
            int i2 = 0;
            long currentTime = System.currentTimeMillis();
            long filterMillis = filterDays > 0 ? (filterDays * 24L * 60L * 60L * 1000L) : 0;

            while (i2 < fileArr.length) {
                if (fileArr[i2].isDirectory()) {
                    String path = fileArr[i2].getPath();
                    File[] fileList = Utils.getFileList(fileArr[i2].getPath());
                    if (fileList != null && fileList.length > 0) {
                        checkFileOfDirectoryImage(path, fileList);
                    }
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = 8;
                    options.inDither = z;
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inTempStorage = new byte[32768];
                    BitmapFactory.decodeFile(fileArr[i2].getPath(), options);

                    if (!(contains || options.outWidth == -1 || options.outHeight == -1)) {
                        File file = new File(fileArr[i2].getPath());
                        long lastModified = file.lastModified();

                        // ✅ Filter by date
                        if (filterDays > 0 && (currentTime - lastModified) > filterMillis) {
                            i2++;
                            continue;
                        }

                        if (isImageFile(file)) {
                            int parseInt = Integer.parseInt(String.valueOf(file.length()));
                            String path2 = fileArr[i2].getPath();
                            PhotoModel photoModel2 = new PhotoModel(path2, lastModified, (long) parseInt);
                            listPhoto.add(photoModel2);
                            number++;
                            publishProgress(number);
                        } else {
                            i = i2;
                            int parseInt2 = Integer.parseInt(String.valueOf(file.length()));
                            if (parseInt2 > 1000) {
                                listPhoto.add(new PhotoModel(fileArr[i].getPath(), file.lastModified(), (long) parseInt2));
                                number++;
                                publishProgress(number);
                                i2 = i + 1;
                                z = false;
                                continue;
                            }
                        }
                    }
                }
                i = i2;
                i2 = i + 1;
                z = false;
            }

            if (this.listPhoto.size() != 0) {
                AlbumPhoto albumPhoto = new AlbumPhoto();
                albumPhoto.str_folder = str;
                albumPhoto.lastModified = new File(str).lastModified();
                Collections.sort(this.listPhoto, (photoModel, photoModel2) ->
                        Long.compare(photoModel2.getLastModified(), photoModel.getLastModified()));
                albumPhoto.listPhoto = (ArrayList<PhotoModel>) this.listPhoto.clone();
                mAlbumPhoto.add(albumPhoto);
            }
            this.listPhoto.clear();
        }


        public boolean isImageFile(File file) {
            String mimeType = getMimeType(file);
            return mimeType != null && mimeType.startsWith("image");
        }

        public boolean isVideoFile(File file) {
            String mimeType = getMimeType(file);
            return mimeType != null && (mimeType.equals("application/x-mpegURL") || mimeType.startsWith("video"));
        }

        public boolean isAudioFile(File file) {
            String mimeType = getMimeType(file);
            return mimeType != null && mimeType.startsWith("audio");
        }

        public void getSdCardImage() {
            // Set your filter days: 0 = no filter, 7 = 7 days, 30 = 1 month
            int filterDays = this.filterDays;
            long filterMillis = filterDays * 24L * 60 * 60 * 1000;
            long currentTime = System.currentTimeMillis();
            String[] externalStorageDirectories = this.getExternalStorageDirectories();
            if (externalStorageDirectories != null && externalStorageDirectories.length > 0) {
                for (String str : externalStorageDirectories) {
                    File file = new File(str);
                    if (file.exists()) {

                        checkFileOfDirectoryImageFiltered(str, file.listFiles(), filterMillis, currentTime);
                    }
                }
            }
        }

        public void checkFileOfDirectoryVideo(String str, File[] fileArr, int filterDays) {
            boolean contains = str.contains(Utils.getPathSave(ScanImagesActivty.this, getString(R.string.restore_folder_path_video)));

            long currentTime = System.currentTimeMillis();
            long filterTime = currentTime - (filterDays > 0 ? filterDays * 24L * 60 * 60 * 1000 : 0);

            if (fileArr != null) {
                for (File value : fileArr) {
                    if (value.isDirectory()) {
                        String path = value.getPath();
//                        tvPath.setText(value.getPath().toString());
                        File[] fileList = Utils.getFileList(value.getPath());
                        if (path != null && fileList != null && fileList.length > 0) {
                            checkFileOfDirectoryVideo(path, fileList, filterDays);
                        }
                    } else {
                        File file = new File(value.getPath());
                        if (!contains && isVideoFile(file)) {
                            long lastModified = file.lastModified();
                            if (filterDays == 0 || lastModified >= filterTime) {
                                String extension = value.getPath().substring(value.getPath().lastIndexOf(".") + 1);
                                long duration = 0;
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                try {
                                    retriever.setDataSource(file.getPath());
                                    String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    duration = Long.parseLong(durationStr != null ? durationStr : "0");
                                } catch (Exception ignored) {
                                } finally {
                                    try {
                                        retriever.release();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                VideoModel videoModel = new VideoModel(
                                        value.getPath(),
                                        lastModified,
                                        file.length(),
                                        extension,
                                        Utils.convertDuration(duration)
                                );
                                this.listVideo.add(videoModel);
                                number++;
                                publishProgress(number);
                            }
                        }
                    }
                }

                if (this.listVideo.size() > 0) {
                    AlbumVideo albumVideo = new AlbumVideo();
                    albumVideo.str_folder = str;
                    albumVideo.lastModified = new File(str).lastModified();
                    Collections.sort(this.listVideo, new Comparator<VideoModel>() {
                        public int compare(VideoModel a, VideoModel b) {
                            return Long.compare(b.getLastModified(), a.getLastModified());
                        }
                    });
                    albumVideo.listPhoto = (ArrayList) this.listVideo.clone();
                    mAlbumVideo.add(albumVideo);
                }

                this.listVideo.clear();
            }
        }


        public void getSdCardVideo(int filterDays) {
            String[] externalStorageDirectories = this.getExternalStorageDirectories();
            if (externalStorageDirectories != null && externalStorageDirectories.length > 0) {
                for (String str : externalStorageDirectories) {
                    File file = new File(str);
                    if (file.exists()) {
                        checkFileOfDirectoryVideo(str, file.listFiles(), filterDays);
                    }
                }
            }
        }


        public void checkFileOfDirectoryAudio(String str, File[] fileArr, int filterDays) {
            boolean contains = str.contains(Utils.getPathSave(ScanImagesActivty.this, getString(R.string.restore_folder_path_audio)));
            long currentTime = System.currentTimeMillis();
            long filterTime = currentTime - (filterDays > 0 ? filterDays * 24L * 60 * 60 * 1000 : 0);

            for (File value : fileArr) {
                if (value.isDirectory()) {
                    String path = value.getPath();
//                    tvPath.setText(value.getPath().toString());
                    File[] fileList = Utils.getFileList(value.getPath());
                    if (fileList != null && fileList.length > 0) {
                        checkFileOfDirectoryAudio(path, fileList, filterDays);
                    }
                } else {
                    File file = new File(value.getPath());
                    if (!contains && isAudioFile(file)) {
                        long lastModified = file.lastModified();
                        long fileSize = file.length();
                        if ((filterDays == 0 || lastModified >= filterTime) && fileSize > 10000) {
                            listAudio.add(new AudioModel(file.getPath(), lastModified, fileSize));
                            number++;
                            publishProgress(number);
                        }
                    }
                }
            }

            if (!listAudio.isEmpty()) {
                AlbumAudio albumAudio = new AlbumAudio();
                albumAudio.str_folder = str;
                albumAudio.lastModified = new File(str).lastModified();
                Collections.sort(this.listAudio, new Comparator<AudioModel>() {
                    public int compare(AudioModel a, AudioModel b) {
                        return Long.compare(b.getLastModified(), a.getLastModified());
                    }
                });
                albumAudio.listPhoto = (ArrayList) this.listAudio.clone();
                mAlbumAudio.add(albumAudio);
            }

            this.listAudio.clear();
        }


        public String[] getExternalStorageDirectories() {
            File[] externalFilesDirs;
            String[] split;
            boolean z;
            ArrayList<String> arrayList = new ArrayList<>();

            if ((externalFilesDirs = getExternalFilesDirs("0")) != null && externalFilesDirs.length > 0) {
                for (File file : externalFilesDirs) {
                    if (!(file == null || (split = file.getPath().split("/Android")) == null || split.length <= 0)) {
                        String str = split[0];
                        z = Environment.isExternalStorageRemovable(file);
                        if (z) {
                            arrayList.add(str);
                        }
                    }
                }
            }
            if (arrayList.isEmpty()) {
                StringBuilder str2 = new StringBuilder();
                try {
                    Process start = new ProcessBuilder().command("mount | grep /dev/block/vold").redirectErrorStream(true).start();
                    start.waitFor();
                    InputStream inputStream = start.getInputStream();
                    byte[] bArr = new byte[1024];
                    while (inputStream.read(bArr) != -1) {
                        str2.append(new String(bArr));
                    }
                    inputStream.close();
                } catch (Exception ignored) {

                }
                if (!str2.toString().trim().isEmpty()) {
                    String[] split2 = str2.toString().split(IOUtils.LINE_SEPARATOR_UNIX);
                    if (split2.length > 0) {
                        for (String split3 : split2) {
                            arrayList.add(split3.split(" ")[2]);
                        }
                    }
                }
            }
            String[] strArr = new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                strArr[i] = arrayList.get(i);
            }
            return strArr;
        }

        public void getSdCardAudio(int filterDays) {
            String[] externalStorageDirectories = getExternalStorageDirectories();
            if (externalStorageDirectories != null && externalStorageDirectories.length > 0) {
                for (String str : externalStorageDirectories) {
                    File file = new File(str);
                    if (file.exists()) {
                        checkFileOfDirectoryAudio(str, file.listFiles(), filterDays);
                    }
                }
            }
        }

    }

    public String getMimeType(File file) {
        Uri fromFile = Uri.fromFile(file);
        if (fromFile.getScheme().equals("content")) {
            return getContentResolver().getType(fromFile);
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fromFile.toString()).toLowerCase());
    }

}