package com.hui.sheepguard;

import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;

import com.dodoo_tech.gfal.app.GFALApp;
import com.dodoo_tech.gfal.utils.LogUtil;
import com.tencent.bugly.Bugly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class BaseApp extends GFALApp {
    private static final String TAG = "BaseApp";

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this,GuardService.class));

        /*String processName = getProcessName();
        Bugly.init(getApplicationContext(), "b67b71c2d4", false);
        if (TextUtils.equals(processName,getPackageName())) {

            //注册保活小绵羊的广播
            //GuardReceiver.registerReceiver(this);

            //开启保活线程
            GuardHandle.start();

            startService(new Intent(this,GuardService.class));
        }*/
    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onTerminate() {
        GuardReceiver.unregisterReceiver(this);
        super.onTerminate();
    }
}
