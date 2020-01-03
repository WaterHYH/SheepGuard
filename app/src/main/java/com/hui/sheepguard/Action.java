package com.hui.sheepguard;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.dodoo_tech.gfal.thread.LooperThread;
import com.dodoo_tech.gfal.utils.LogUtil;

import java.util.Random;


public class Action {

    private static final String TAG = "Action";

    public synchronized static void click(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            LogUtil.logWarn(TAG,"click","nodeInfo is null");
            return;
        }
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        Action.click(rect);
    }

    public synchronized static void longClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            LogUtil.logWarn(TAG,"longClick","nodeInfo is null");
            return;
        }
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        Action.longClick(rect);
    }

    public synchronized static void longClick(Rect rect) {
        Action.longClick(rect.centerX(), rect.centerY());
    }

    public synchronized static void click(Rect rect) {
        Action.click(rect.centerX(), rect.centerY());
    }

    public synchronized static void click(int x, int y){
        click(x,y,ViewConfiguration.getTapTimeout() + 50);
    }

    public synchronized static void longClick(int x, int y){
        click(x,y,ViewConfiguration.getLongPressTimeout() + 200);
    }

    public synchronized static void click(int x, int y, long duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LogUtil.logInfo(TAG, "click", "x=" + x + " y=" + y);
            if (x < 0 || y < 0){
                LogUtil.logWarn(TAG,"click"," x or y not less than zero");
            }else {
                gesture(0, duration,duration+300, new int[]{x, y});
            }
        }
    }

    public static void swipe(int x1, int y1, int x2, int y2) {
        //根据滑动距离计算delay值
        int delay = (Math.abs(x1-x2)+Math.abs(y1-y2))/2*3;
        if (delay < 300){
            delay = 300;
        }
        if (delay > 1000) {
            delay = 1000;
        }
        swipe(x1,y1,x2,y2,delay);
    }

    public static void swipe(int x1, int y1, int x2, int y2,long delay){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gesture(0, delay,delay+300, new int[]{x1, y1}, new int[]{x2, y2});
            //gesture(0, delay,delay+300, toRealisticPath((int) delay,x1,y1,x2,y2));
        }
    }

    private static Handler handler;
    private static final class GestureThread implements Runnable{
        GestureDescription.StrokeDescription[] descriptions;
        long sleep;

        public GestureThread(long sleep, GestureDescription.StrokeDescription... strokes) {
            this.descriptions = strokes;
            this.sleep = sleep;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            Action.gestures(sleep,descriptions);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized static void gesture(long start, long duration, long sleep, int[]... points) {
        Path path = pointsToPath(points);
        if (handler == null) {
            handler = new Handler(new LooperThread(Thread.MAX_PRIORITY).getMyLooper());
        }
        handler.post(new GestureThread(sleep,new GestureDescription.StrokeDescription(path, start, duration)));
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized static void gesture(long start, long duration, long sleep, Path path) {
        if (handler == null) {
            handler = new Handler(new LooperThread(Thread.MAX_PRIORITY).getMyLooper());
        }
        handler.post(new GestureThread(sleep,new GestureDescription.StrokeDescription(path, start, duration)));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void gestures(long sleep,GestureDescription.StrokeDescription... strokes) {
        try {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            for (GestureDescription.StrokeDescription stroke : strokes) {
                builder.addStroke(stroke);
            }
            GestureDescription description = builder.build();
            MainAccessibility.instance.dispatchGesture(description, null, null);

            Thread.sleep(sleep);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }

    private static Path pointsToPath(int[]... points) {
        Path path = new Path();
        path.moveTo(points[0][0], points[0][1]);
        for (int i = 1; i < points.length; i++) {
            int[] point = points[i];
            path.lineTo(point[0], point[1]);
        }
        return path;
    }

    private static Path toRealisticPath(int duration,int x1,int y1,int x2,int y2) {
        if (duration < 0){
            duration = 0;
        }
        int count = duration/30;
        int[][] points = new int[count+2][2];
        points[0] = new int[]{x1,y1};
        if (count > 0) {
            int xOffset = (x2 - x1)/count;
            int yOffset = (y2 - y1)/count;
            for (int i = 0; i < count; i++) {
                points[i+1][0] = x1 + xOffset*i;
                points[i+1][1] = y1 + yOffset*i;
            }
        }
        points[count+1] = new int[]{x2,y2};
        LogUtil.logInfo(TAG,"toRealisticPath count="+ count);
        return pointsToPath(points);
    }

    public static void back() {
        if (handler == null) {
            handler = new Handler(new LooperThread(Thread.MAX_PRIORITY).getMyLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.logInfo(TAG, "back");
                MainAccessibility.instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                GlobalMethod.ThreadSleep(500);
            }
        });
    }

    public static void powerDialog() {
        if (handler == null) {
            handler = new Handler(new LooperThread(Thread.MAX_PRIORITY).getMyLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.logInfo(TAG, "powerDialog");
                MainAccessibility.instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
                GlobalMethod.ThreadSleep(500);
            }
        });
    }

    public static void recents() {
        if (handler == null) {
            handler = new Handler(new LooperThread(Thread.MAX_PRIORITY).getMyLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.logInfo(TAG, "recents");
                MainAccessibility.instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                GlobalMethod.ThreadSleep(500);
            }
        });
    }

    public static void home() {
        if (handler == null) {
            handler = new Handler(new LooperThread(Thread.MAX_PRIORITY).getMyLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.logInfo(TAG, "home");
                if (MainAccessibility.instance != null) {
                    MainAccessibility.instance.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                    GlobalMethod.ThreadSleep(500);
                }else {
                    LogUtil.logError(TAG, "home","MainAccessibility is null");
                }
            }
        });
    }

    public static void swipeAndClickScreen(int swipeCount, int clickOffsetX, int clickOffsetY) {
        int centerX = ScreenMetrics.getDeviceScreenWidth() / 2;
        int centerY = ScreenMetrics.getDeviceScreenHeight() / 2;
        swipeScreen(swipeCount);
        click(centerX + clickOffsetX, centerY + clickOffsetY);
    }

    public static void swipeScreen(int swipeCount) {
        int centerX = ScreenMetrics.getDeviceScreenWidth() / 2;
        int centerY = ScreenMetrics.getDeviceScreenHeight() / 2;
        swipeVertical(swipeCount, centerX, centerY + 300, centerY - 300);
    }

    public static void swipeScreen(int swipeCount,int offsetY) {
        int centerX = ScreenMetrics.getDeviceScreenWidth() / 2;
        int centerY = ScreenMetrics.getDeviceScreenHeight() / 2;
        swipeVertical(swipeCount, centerX, centerY + offsetY/2, centerY - offsetY/2);
    }

    public static void swipeScreen(int swipeCount,int x,int offsetY) {
        int centerY = ScreenMetrics.getDeviceScreenHeight() / 2;
        swipeVertical(swipeCount, x, centerY + offsetY/2, centerY - offsetY/2);
    }

    public static void swipeScreen(int swipeCount,int offsetY,long delay) {
        int centerX = ScreenMetrics.getDeviceScreenWidth() / 2;
        int centerY = ScreenMetrics.getDeviceScreenHeight() / 2;
        swipeVertical(swipeCount, centerX, centerY + offsetY/2, centerY - offsetY/2,delay);
    }

    public static void clickIfInScreen(AccessibilityNodeInfo nodeInfo,int offsetLeft,int offsetRight,int offsetTop, int offsetBottom){
        if (GlobalMethod.centerInScreen(nodeInfo,offsetLeft,offsetRight,offsetTop,offsetBottom)) {
            click(nodeInfo);
        }
    }

    public static void swipeVertical(int swipeCount, int x, int startY, int endY) {
        for (int i = 0; i < swipeCount; i++) {
            swipe(x, startY, x, endY);
        }
    }

    public static void swipeVertical(int swipeCount, int x, int startY, int endY,long delay) {
        for (int i = 0; i < swipeCount; i++) {
            swipe(x, startY, x, endY,delay);
        }
    }

    public static void swipeHorizontal(int swipeCount, int startX, int endX, int y) {
        for (int i = 0; i < swipeCount; i++) {
            swipe(startX, y, endX, y);
        }
    }

    public static void swipeVerticalRandom(int offset,int bound,int upRate){
        int centerX = ScreenMetrics.getDeviceScreenWidth()/2;
        swipeVerticalRandom(centerX,offset,bound,upRate);
    }

    public static void swipeVerticalRandom(int x,int offsetY,int bound,int upRate){
        int centerY = ScreenMetrics.getDeviceScreenHeight()/2;
        offsetY = offsetY/2;
        if (new Random().nextInt(bound) >= upRate){
            swipeVertical(1,x,centerY+offsetY,centerY-offsetY);
        }else {
            swipeVertical(1,x,centerY-offsetY,centerY+offsetY);
        }
    }
}
