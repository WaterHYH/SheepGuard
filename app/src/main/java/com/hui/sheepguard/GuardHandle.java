package com.hui.sheepguard;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.dodoo_tech.gfal.thread.LooperThread;
import com.dodoo_tech.gfal.utils.AppUtils;
import com.dodoo_tech.gfal.utils.LogUtil;

/**
 * @author Created by waterHYH on 2020-01-03.
 */
public class GuardHandle extends Handler {
    public static final String TAG = "GuardHandle";
    public static final String SHEEP_PACKAGE = "com.hui.autobrush";
    public static final String SHEEP_NAME = "小绵羊";
    public static final String MY_ACTION = "com.hui.sheepguard.action.beat";
    public static final String BEGIN_ACTION = "com.hui.sheepguard.action.restart";
    private static GuardHandle instance;
    private int sheepState;//1=运行中，2=设置无障碍
    private long sheepBeatTime;

    public long getSheepBeatTime() {
        return sheepBeatTime;
    }

    public static void setSheepBeatTime(long sheepBeatTime) {
        if (instance != null) {
            instance.sheepBeatTime = sheepBeatTime;
            if (instance.sheepState != 1) {
                LogUtil.logInfo(TAG,"setSheepBeatTime","sheep is running");
            }
            if (instance.sheepState == 3) {
                instance.startSheep();
            }
            instance.sheepState = 1;
        }
    }

    private void startSheep() {
        LogUtil.logInfo(TAG,"启动"+SHEEP_NAME);
        AppUtils.launchApp(SHEEP_PACKAGE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BEGIN_ACTION);
                intent.setPackage(SHEEP_PACKAGE);
                BaseApp.getContext().sendBroadcast(intent);
            }
        },3000);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BEGIN_ACTION);
                intent.setPackage(SHEEP_PACKAGE);
                BaseApp.getContext().sendBroadcast(intent);
            }
        },5000);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BEGIN_ACTION);
                intent.setPackage(SHEEP_PACKAGE);
                BaseApp.getContext().sendBroadcast(intent);
            }
        },10000);
    }

    public GuardHandle() {
        super(new LooperThread(Thread.MAX_PRIORITY).getMyLooper());
    }

    public static void start(){
        if (instance == null) {
            instance = new GuardHandle();
            instance.startBeat();
        }
    }

    private void startBeat() {
        LogUtil.logInfo(TAG,"startBeat");
        //checkBeat();
        sendBeat();
    }

    private void checkBeat(){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                checkBeat();
                try{
                    checkSheepState();
                }catch (Exception e){
                    LogUtil.logError(e);
                }
            }
        },500);
    }

    private void checkSheepState() {
        if (System.currentTimeMillis() - getSheepBeatTime() > 1000) {
            if (sheepState == 1) {
                LogUtil.logWarn(TAG,"checkSheepState","sheep is stop");
                sheepState = 2;
                toRestartSheep();
            }
        }
    }

    private void toRestartSheep() {
        if (sheepState == 1) {
            return;
        }
        if (MainAccessibility.instance == null) {
            return;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    restartSheep();
                }catch (Exception e){
                    LogUtil.logError(e);
                }
                toRestartSheep();
            }
        },2000);
    }

    private void restartSheep() {
        switch (sheepState){
            case 2:
                //开启无障碍功能
                AccessibilityNodeInfo alertTitle = GlobalMethod.findNodeInfoByViewId("miui:id/alertTitle");
                if (alertTitle != null && !TextUtils.isEmpty(alertTitle.getText())) {
                    AccessibilityNodeInfo okView = GlobalMethod.findNodeInfoByViewId("android:id/button1");
                    if (okView != null) {
                        if (alertTitle.getText().toString().contains("开启")) {
                            sheepState = 3;
                        }
                        Action.click(okView);
                        break;
                    }
                }

                AccessibilityNodeInfo titleView = GlobalMethod.findNodeInfoByViewId("miui:id/action_bar_title");
                if (titleView != null && TextUtils.equals(titleView.getText(),SHEEP_NAME)) {
                    AccessibilityNodeInfo checkBox = GlobalMethod.findNodeInfoByViewId("android:id/checkbox");
                    if (checkBox != null) {
                        Action.click(checkBox);
                        break;
                    }
                }
                if (titleView != null && TextUtils.equals(titleView.getText(),"无障碍")) {
                    AccessibilityNodeInfo sheepView = GlobalMethod.findNodeInfoDeepByText(SHEEP_NAME);
                    if (sheepView != null) {
                        Action.click(sheepView);
                        break;
                    }
                }

                if (!hasOpenSetting) {
                    //打开无障碍设置界面
                    try{
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        BaseApp.getContext().startActivity(intent);
                        hasOpenSetting = true;
                    }catch (Exception e){
                        LogUtil.logError(e);
                    }
                }else {
                    hasOpenSetting = false;
                    Action.back();
                }

                break;
            case 3:
                startSheep();
                break;
        }
    }

    private boolean hasOpenSetting;

    private void sendBeat(){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBeat();
                try{
                    sendMyState();
                }catch (Exception e){
                    LogUtil.logError(e);
                }
            }
        },500);
    }

    private void sendMyState() {
        Intent intent = new Intent(MY_ACTION);
        intent.setPackage(SHEEP_PACKAGE);
        BaseApp.getContext().sendBroadcast(intent);
    }
}
