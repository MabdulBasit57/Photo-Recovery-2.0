package com.recover.photo.ui.activity;

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

import com.recover.photo.pj.AlbumAudio;
import com.recover.photo.pj.AlbumPhoto;
import com.recover.photo.pj.AlbumVideo;
import com.recover.photo.pj.AudioModel;
import com.recover.photo.pj.PhotoModel;
import com.recover.photo.pj.VideoModel;
import com.recover.photo.R;
import com.recover.photo.utils.Utils;
import com.skyfishjy.library.RippleBackground;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScanImagesActivty extends AppCompatActivity {

    TextView tvNumber;
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
            this.mScanAsyncTask.execute(new Void[i]);
            return;
        }
        Toast.makeText(this, getString(R.string.scan_wait), Toast.LENGTH_LONG).show();
    }

    public class ScanAsyncTask extends AsyncTask<Void, Integer, Void> {
        ArrayList<AudioModel> listAudio = new ArrayList<>();
        ArrayList<PhotoModel> listPhoto = new ArrayList<>();
        ArrayList<VideoModel> listVideo = new ArrayList<>();
        int number = 0;
        int typeScan = 0;

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
            Log.d("absolutePath =", "" + absolutePath);
            absolutePath = file.getAbsoluteFile().getParent();
            Log.d("absolutePath 1=", "" + absolutePath);

            StringBuilder sb = new StringBuilder();
            sb.append("root = ");
            sb.append(absolutePath);
            if (this.typeScan == 0) {
                try {
                    getSdCardImage();
                    Log.d("ssss", "" + Utils.getFileList(absolutePath).length);
                    checkFileOfDirectoryImage(absolutePath, Utils.getFileList(absolutePath));
                } catch (Exception unused) {
                    unused.printStackTrace();
                }
                Collections.sort(mAlbumPhoto, new Comparator<AlbumPhoto>() {
                    public int compare(AlbumPhoto albumPhoto, AlbumPhoto albumPhoto2) {
                        return Long.compare(albumPhoto2.lastModified, albumPhoto.lastModified);
                    }
                });
            }
            if (this.typeScan == 1) {
                getSdCardVideo();
                checkFileOfDirectoryVideo(absolutePath, Utils.getFileList(absolutePath));
                Collections.sort(mAlbumVideo, new Comparator<AlbumVideo>() {
                    public int compare(AlbumVideo albumVideo, AlbumVideo albumVideo2) {
                        return Long.compare(albumVideo2.lastModified, albumVideo.lastModified);
                    }
                });
            }
            if (this.typeScan == 2) {
                try {
                    getSdCardAudio();
                    checkFileOfDirectoryAudio(absolutePath, Utils.getFileList(absolutePath));
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

        public void checkFileOfDirectoryImage(String str, File[] fileArr) {
            int i;
            boolean contains = str.contains(Utils.getPathSave(ScanImagesActivty.this, getString(R.string.restore_folder_path_photo)));
            boolean z = false;
            int i2 = 0;
            while (i2 < fileArr.length) {
                if (fileArr[i2].isDirectory()) {
                    String path = fileArr[i2].getPath();
                    File[] fileList = Utils.getFileList(fileArr[i2].getPath());
                    if (!(fileList == null || fileList.length <= 0)) {
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
                        if (isImageFile(file)) {
                            int parseInt = Integer.parseInt(String.valueOf(file.length()));
                            ArrayList<PhotoModel> arrayList = this.listPhoto;
                            String path2 = fileArr[i2].getPath();
                            long lastModified = file.lastModified();
                            i = i2;
                            PhotoModel photoModel2 = new PhotoModel(path2, lastModified, (long) parseInt);
                            arrayList.add(photoModel2);
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
                            }
                        }
                        i2 = i + 1;
                        z = false;
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
                Collections.sort(this.listPhoto, (photoModel, photoModel2) -> Long.compare(photoModel2.getLastModified(), photoModel.getLastModified()));
                albumPhoto.listPhoto = (ArrayList) this.listPhoto.clone();
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
            String[] externalStorageDirectories = this.getExternalStorageDirectories();
            if (externalStorageDirectories != null && externalStorageDirectories.length > 0) {
                for (String str : externalStorageDirectories) {
                    File file = new File(str);
                    if (file.exists()) {
                        checkFileOfDirectoryImage(str, file.listFiles());
                    }
                }
            }
        }

        public void checkFileOfDirectoryVideo(String str, File[] fileArr) {
            boolean contains = str.contains(Utils.getPathSave(ScanImagesActivty.this, getString(R.string.restore_folder_path_video)));
            if (fileArr != null) {
                for (File value : fileArr) {
                    if (value.isDirectory()) {
                        String path = value.getPath();
                        File[] fileList = Utils.getFileList(value.getPath());
                        if (!(path == null || fileList == null || fileList.length <= 0)) {
                            checkFileOfDirectoryVideo(path, fileList);
                        }
                    } else {
                        File file = new File(value.getPath());
                        if (!contains && isVideoFile(file)) {
                            String substring = value.getPath().substring(value.getPath().lastIndexOf(".") + 1);
                            long j = 0;
                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            try {
                                mediaMetadataRetriever.setDataSource(file.getPath());
                                j = Long.parseLong(mediaMetadataRetriever.extractMetadata(9));
                                mediaMetadataRetriever.release();
                            } catch (Exception ignored) {
                            }
                            ArrayList<VideoModel> arrayList = this.listVideo;
                            VideoModel videoModel2 = new VideoModel(value.getPath(), file.lastModified(), file.length(), substring, Utils.convertDuration(j));
                            arrayList.add(videoModel2);
                            number++;
                            publishProgress(number);
                        }
                    }
                }
                if (this.listVideo.size() != 0) {
                    AlbumVideo albumVideo = new AlbumVideo();
                    albumVideo.str_folder = str;
                    albumVideo.lastModified = new File(str).lastModified();
                    Collections.sort(this.listVideo, new Comparator<VideoModel>() {
                        public int compare(VideoModel videoModel, VideoModel videoModel2) {
                            return Long.valueOf(videoModel2.getLastModified()).compareTo(Long.valueOf(videoModel.getLastModified()));
                        }
                    });
                    albumVideo.listPhoto = (ArrayList) this.listVideo.clone();
                    mAlbumVideo.add(albumVideo);
                }
                this.listVideo.clear();
            }
        }

        public void getSdCardVideo() {
            String[] externalStorageDirectories = this.getExternalStorageDirectories();
            if (externalStorageDirectories != null && externalStorageDirectories.length > 0) {
                for (String str : externalStorageDirectories) {
                    File file = new File(str);
                    if (file.exists()) {
                        checkFileOfDirectoryVideo(str, file.listFiles());
                    }
                }
            }
        }

        public void checkFileOfDirectoryAudio(String str, File[] fileArr) {
            int parseInt;
            boolean contains = str.contains(Utils.getPathSave(ScanImagesActivty.this, getString(R.string.restore_folder_path_audio)));
            for (File value : fileArr) {
                if (value.isDirectory()) {
                    String path = value.getPath();
                    File[] fileList = Utils.getFileList(value.getPath());
                    if (!(fileList == null || fileList.length <= 0)) {
                        checkFileOfDirectoryAudio(path, fileList);
                    }
                } else {
                    File file = new File(value.getPath());
                    if (!contains && isAudioFile(file) && (parseInt = Integer.parseInt(String.valueOf(file.length()))) > 10000) {
                        listAudio.add(new AudioModel(value.getPath(), file.lastModified(), (long) parseInt));
                        number++;
                        publishProgress(number);
                    }
                }
            }
            if (this.listAudio.size() != 0) {
                AlbumAudio albumAudio = new AlbumAudio();
                albumAudio.str_folder = str;
                albumAudio.lastModified = new File(str).lastModified();
                Collections.sort(this.listAudio, new Comparator<AudioModel>() {
                    public int compare(AudioModel audioModel, AudioModel audioModel2) {
                        return Long.valueOf(audioModel2.getLastModified()).compareTo(Long.valueOf(audioModel.getLastModified()));
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

        public void getSdCardAudio() {
            String[] externalStorageDirectories = getExternalStorageDirectories();
            if (externalStorageDirectories != null && externalStorageDirectories.length > 0) {
                for (String str : externalStorageDirectories) {
                    File file = new File(str);
                    if (file.exists()) {
                        checkFileOfDirectoryAudio(str, file.listFiles());
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