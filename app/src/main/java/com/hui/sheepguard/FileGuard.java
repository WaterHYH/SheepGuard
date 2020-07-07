package com.hui.sheepguard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.format.DateUtils;

import androidx.core.app.ActivityCompat;

import com.dodoo_tech.gfal.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileGuard {
    private static final String TAG = "FileGuard";
    private FileLock fileLock;
    private FileChannel fileChannel;
    private String pName;

    public FileGuard() {
        this.pName = BaseApp.getProcessName();
    }

    public void lock(){
        if (BaseApp.getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            LogUtil.logInfo(TAG,"lock","has not permission");

        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sheep_lock.txt");
        try {
            FileOutputStream outputStream = new FileOutputStream(file,true);
            fileChannel = outputStream.getChannel();
            fileLock = fileChannel.lock();
            String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(new Date()) + " " + pName + "\n";
            outputStream.write(str.getBytes());
            LogUtil.logInfo(TAG,"lock",str);
        }catch (Exception e){
        	LogUtil.logError(e);
        }
    }


    public void unLock(){
        LogUtil.logInfo(TAG,"unLock");
        if (fileLock != null) {
            try {
                fileLock.release();
                fileLock = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (fileChannel != null) {
            try {
                fileChannel.close();
                fileChannel = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
