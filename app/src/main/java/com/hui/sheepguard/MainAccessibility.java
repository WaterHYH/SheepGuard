package com.hui.sheepguard;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.dodoo_tech.gfal.utils.LogUtil;

public class MainAccessibility extends AccessibilityService {
    private static final String TAG = "MainAccessibility";

    private static String previousView;
    private static String currentView;
    private static String currentApp;
    private static String currentDialog;

    public static MainAccessibility instance;

    public static String getPreviousView() {
        return previousView;
    }

    public static void setPreviousView(String previousView) {
        MainAccessibility.previousView = previousView;
    }

    public static String getCurrentView() {
        return currentView;
    }

    public static void setCurrentView(String currentView) {
        setPreviousView(MainAccessibility.currentView);
        MainAccessibility.currentView = currentView;
        LogUtil.logInfo(TAG, "setCurrentView", currentView);
    }

    public static String getCurrentApp() {
        return currentApp;
    }

    public static void setCurrentApp(String currentApp) {
        MainAccessibility.currentApp = currentApp;
    }

    public static String getCurrentDialog() {
        return currentDialog;
    }

    public static void setCurrentDialog(String currentDialog) {
        MainAccessibility.currentDialog = currentDialog;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.logInfo(TAG,"onCreate");
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLowMemory() {
        LogUtil.logWarn(TAG,"onLowMemory","");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        LogUtil.logWarn(TAG,"onTrimMemory","level="+level);
        super.onTrimMemory(level);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            handlerWindowStateChange(event);
        }
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        return super.onKeyEvent(event);
    }

    private void handlerWindowStateChange(AccessibilityEvent event) {
        try {
            String app = event.getPackageName().toString();
            String view = event.getClassName().toString();
            LogUtil.logInfo(TAG, "handlerWindowStateChange", "app=" + app + " view=" + view);
            setCurrentApp(app);
            setCurrentView(view);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }

    @Override
    public void onInterrupt() {
        LogUtil.logInfo(TAG,"onInterrupt");

    }
}
