package com.hui.sheepguard;

import com.dodoo_tech.gfal.app.GFALApp;
import com.dodoo_tech.gfal.utils.LogUtil;
import com.tencent.bugly.Bugly;

public class BaseApp extends GFALApp {
    private static final String TAG = "BaseApp";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.logInfo(TAG,"onCreate");
        Bugly.init(getApplicationContext(), "b67b71c2d4", false);

        //注册保活小绵羊的广播
        GuardReceiver.registerReceiver(this);

        //开启保活线程
        GuardHandle.start();

    }

    @Override
    public void onTerminate() {
        GuardReceiver.unregisterReceiver(this);
        super.onTerminate();
    }
}
