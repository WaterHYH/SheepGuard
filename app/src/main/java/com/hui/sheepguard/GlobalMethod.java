package com.hui.sheepguard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.RequiresApi;

import com.dodoo_tech.gfal.utils.LogUtil;
import com.dodoo_tech.gfal.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * @author Created by waterHYH on 2019-06-28.
 */
public class GlobalMethod {

    public static String getForeApp() {
        return MainAccessibility.getCurrentApp();
    }

    public static String getForeView() {
        //android5.0以下版本的获取方法
        /*if (Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT) {
            ActivityManager activityManager = (ActivityManager) GFALApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
            if (taskInfos != null && !taskInfos.isEmpty()) {
                ComponentName componentName = taskInfos.get(0).topActivity;
                if (componentName != null) {
                    return componentName.getClassName();
                }
            }
        }*/
        return MainAccessibility.getCurrentView();
    }

    public static String getPreviousView() {
        return MainAccessibility.getPreviousView();
    }

    public static AccessibilityNodeInfo findNodeInfoByIdentity(String identity) {
        if (TextUtils.isEmpty(identity)) {
            return null;
        }
        AccessibilityNodeInfo nodeInfo = findNodeInfoByViewId(identity);
        if (nodeInfo == null) {
            nodeInfo = findNodeInfoByText(identity);
        }
        return nodeInfo;
    }

    public static AccessibilityNodeInfo findNodeInfoByViewId(String id) {
        return findNodeInfoByViewId(id, 0);
    }

    public static AccessibilityNodeInfo findNodeInfoByText(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            AccessibilityNodeInfo rootNodeInfo = getRoot();
            return findNodeInfoByText(rootNodeInfo, text);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfoByViewId(String id, int index) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        try {
            AccessibilityNodeInfo rootNodeInfo = getRoot();
            return findNodeInfoByViewId(rootNodeInfo, id, index);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return null;
    }

    public static AccessibilityNodeInfo getRoot() {
        try {
            AccessibilityNodeInfo rootNodeInfo = MainAccessibility.instance.getRootInActiveWindow();
            if (rootNodeInfo != null) {
                return rootNodeInfo;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                List<AccessibilityWindowInfo> windowInfos = MainAccessibility.instance.getWindows();
                if (windowInfos != null) {
                    for (AccessibilityWindowInfo windowInfo : windowInfos) {
                        rootNodeInfo = windowInfo.getRoot();
                        if (rootNodeInfo != null) {
                            return rootNodeInfo;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfoByText(AccessibilityNodeInfo nodeInfo, String text) {
        if (TextUtils.isEmpty(text) || nodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfos != null && nodeInfos.size() > 0) {
            return nodeInfos.get(0);
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfoByViewId(AccessibilityNodeInfo nodeInfo, String id, int index) {
        if (TextUtils.isEmpty(id) || nodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfos != null && nodeInfos.size() > index) {
            return nodeInfos.get(index);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static AccessibilityNodeInfo findNodeInfoByViewId(List<AccessibilityWindowInfo> windowInfos, String id, int index) {
        if (TextUtils.isEmpty(id) || windowInfos == null) {
            return null;
        }
        for (AccessibilityWindowInfo windowInfo : windowInfos) {
            AccessibilityNodeInfo rootNodeInfo = windowInfo.getRoot();
            if (rootNodeInfo != null) {
                return findNodeInfoByViewId(rootNodeInfo, id, index);
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<AccessibilityNodeInfo> findNodeInfosByViewId(List<AccessibilityWindowInfo> windowInfos, String id) {
        List<AccessibilityNodeInfo> result = new ArrayList<>();
        if (windowInfos == null) {
            return result;
        }
        for (AccessibilityWindowInfo windowInfo : windowInfos) {
            AccessibilityNodeInfo rootNodeInfo = windowInfo.getRoot();
            if (rootNodeInfo != null) {
                return findNodeInfosByViewId(rootNodeInfo, id);
            }
        }
        return result;
    }

    public static List<AccessibilityNodeInfo> findNodeInfosByViewId(AccessibilityNodeInfo nodeInfo, String id) {
        List<AccessibilityNodeInfo> result = new ArrayList<>();
        if (TextUtils.isEmpty(id) || nodeInfo == null) {
            return result;
        }
        result = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * 判断指定{@link AccessibilityNodeInfo}是否完全显示在屏幕的指定区域中
     *
     * @param nodeInfo     指定{@link AccessibilityNodeInfo}
     * @param offsetLeft   指定区域左侧距离屏幕左侧的距离
     * @param offsetRight  指定区域右侧距离屏幕右侧的距离
     * @param offsetTop    指定区域顶部距离屏幕顶部的距离
     * @param offsetBottom 指定区域底部距离屏幕底部的距离
     * @return 是否在指定区域中
     */
    public static boolean allInScreen(AccessibilityNodeInfo nodeInfo, int offsetLeft, int offsetRight, int offsetTop, int offsetBottom) {
        if (nodeInfo == null || !nodeInfo.isVisibleToUser()) {
            return false;
        }
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        return allInScreen(rect, offsetLeft, offsetRight, offsetTop, offsetBottom);
    }

    public static boolean allInScreen(Rect rect, int offsetLeft, int offsetRight, int offsetTop, int offsetBottom) {
        if (rect == null) {
            return false;
        }
        if (rect.left < offsetLeft
                || rect.right > ScreenMetrics.getDeviceScreenWidth() - offsetRight
                || rect.top < offsetTop
                || rect.bottom > ScreenMetrics.getDeviceScreenHeight() - offsetBottom) {
            return false;
        }
        return true;
    }

    public static boolean allInBounds(Rect rect, int left, int right, int top, int bottom) {
        if (rect == null) {
            return false;
        }
        if (rect.left < left
                || rect.right > right
                || rect.top < top
                || rect.bottom > bottom) {
            return false;
        }
        return true;
    }

    public static List<AccessibilityNodeInfo> findNodeInfosInScreenByViewId(String id, int offsetLeft, int offsetRight, int offsetTop, int offsetBottom) {
        List<AccessibilityNodeInfo> result = new ArrayList<>();
        List<AccessibilityNodeInfo> nodeInfos = findNodeInfosByViewId(id);
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            if (centerInScreen(nodeInfo, offsetLeft, offsetRight, offsetTop, offsetBottom)) {
                result.add(nodeInfo);
            }
        }
        return result;
    }

    public static List<AccessibilityNodeInfo> findNodeInfosByViewId(String id) {
        if (TextUtils.isEmpty(id)) {
            return new ArrayList<>();
        }
        try {
            AccessibilityNodeInfo rootNodeInfo = getRoot();
            return findNodeInfosByViewId(rootNodeInfo, id);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return new ArrayList<>();
    }

    /**
     * 判断指定{@link AccessibilityNodeInfo}的中心点是否显示在屏幕的指定区域中
     *
     * @param nodeInfo     指定{@link AccessibilityNodeInfo}
     * @param offsetLeft   指定区域左侧距离屏幕左侧的距离
     * @param offsetRight  指定区域右侧距离屏幕右侧的距离
     * @param offsetTop    指定区域顶部距离屏幕顶部的距离
     * @param offsetBottom 指定区域底部距离屏幕底部的距离
     * @return 中心点是否在指定区域中
     */
    public static boolean centerInScreen(AccessibilityNodeInfo nodeInfo, int offsetLeft, int offsetRight, int offsetTop, int offsetBottom) {
        if (nodeInfo == null || !nodeInfo.isVisibleToUser()) {
            return false;
        }
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        if (rect.centerX() < offsetLeft
                || rect.centerX() > ScreenMetrics.getDeviceScreenWidth() - offsetRight
                || rect.centerY() < offsetTop
                || rect.centerY() > ScreenMetrics.getDeviceScreenHeight() - offsetBottom) {
            return false;
        }
        return true;
    }

    public static AccessibilityNodeInfo findNodeInfoInScreenByViewId(String id, int offsetLeft, int offsetRight, int offsetTop, int offsetBottom) {
        List<AccessibilityNodeInfo> nodeInfos = findNodeInfosByViewId(id);
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            if (centerInScreen(nodeInfo, offsetLeft, offsetRight, offsetTop, offsetBottom)) {
                return nodeInfo;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static AccessibilityNodeInfo findNodeInfoByText(List<AccessibilityWindowInfo> windowInfos, String text) {
        if (TextUtils.isEmpty(text) || windowInfos == null) {
            return null;
        }
        for (AccessibilityWindowInfo windowInfo : windowInfos) {
            AccessibilityNodeInfo rootNodeInfo = windowInfo.getRoot();
            if (rootNodeInfo != null) {
                return findNodeInfoByText(rootNodeInfo, text);
            }
        }
        return null;
    }

    /**
     * 根据text深度查找
     *
     * @param text
     * @return
     */
    public static AccessibilityNodeInfo findNodeInfoDeepByText(String text) {
        return findNodeInfoDeepByText(getRoot(), text);
    }

    /**
     * 根据text深度查找
     *
     * @param text
     * @return
     */
    public static AccessibilityNodeInfo findNodeInfoDeepByText(AccessibilityNodeInfo rootView, String text) {
        List<AccessibilityNodeInfo> nodeInfos = findNodeInfosDeepByText(rootView, text, false);
        if (nodeInfos != null && nodeInfos.size() > 0) {
            return nodeInfos.get(0);
        }
        return null;
    }

    /**
     * 根据text深度查找
     *
     * @param root
     * @param text
     * @return
     */
    public static List<AccessibilityNodeInfo> findNodeInfosDeepByText(AccessibilityNodeInfo root, final String text, boolean all) {
        return findNodeInfos(root, all, new NodeInfoComparable() {
            @Override
            public boolean compareTo(AccessibilityNodeInfo nodeInfo) {
                return TextUtils.equals(nodeInfo.getText(), text);
            }
        });
    }



    public synchronized static List<AccessibilityNodeInfo> findNodeInfos(AccessibilityNodeInfo rootView, boolean all, NodeInfoComparable Comparator) {
        List<AccessibilityNodeInfo> result = new ArrayList<>();
        if (rootView == null || Comparator == null) {
            return result;
        }
        Stack<AccessibilityNodeInfo> stack = new Stack<>();
        stack.push(rootView);
        while (!stack.empty()) {
            AccessibilityNodeInfo item = stack.pop();
            //不知道为什么，item有时候为null
            if (item == null) {
                continue;
            }
            try {
                if (Comparator.compareTo(item)) {
                    result.add(item);
                    if (!all) {
                        return result;
                    }
                }
                for (int i = item.getChildCount()-1; i >= 0; i--) {
                    stack.push(item.getChild(i));
                }
            } catch (Exception e) {
                LogUtil.logError(e);
            }
        }
        return result;
    }

    private static Rect tmpRect = new Rect();
    public synchronized static List<AccessibilityNodeInfo> findNodeInfosInBounds(AccessibilityNodeInfo root, final int left, final int right, final int top, final int bottom, boolean all){
        return findNodeInfos(root, all, new NodeInfoComparable() {
            @Override
            public boolean compareTo(AccessibilityNodeInfo nodeInfo) {
                nodeInfo.getBoundsInScreen(tmpRect);
                return allInBounds(tmpRect,left,right,top,bottom);
            }
        });
    }
    public synchronized static List<AccessibilityNodeInfo> findNodeInfosInBounds(final int left, final int right, final int top, final int bottom){
        return findNodeInfosInBounds(getRoot(),left,right,top,bottom,true);
    }

    /**
     * 根据text深度查找
     *
     * @param text
     * @return
     */
    public static List<AccessibilityNodeInfo> findNodeInfosDeepByText(String text) {
        return findNodeInfosDeepByText(getRoot(), text);
    }

    public static List<AccessibilityNodeInfo> findNodeInfosDeepByText(AccessibilityNodeInfo root, String text) {
        return findNodeInfosDeepByText(root, text, true);
    }

    public static AccessibilityNodeInfo findNodeInfoByResourceName(String name) {
        return findNodeInfoByResourceName(getRoot(), name);
    }

    public static AccessibilityNodeInfo findNodeInfoByResourceName(AccessibilityNodeInfo nodeInfo, String name) {
        List<AccessibilityNodeInfo> list = findNodeInfosByResourceName(nodeInfo, name, false);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findNodeInfosByResourceName(AccessibilityNodeInfo root, final String name, boolean all) {
        return findNodeInfos(root, all, new NodeInfoComparable() {
            @Override
            public boolean compareTo(AccessibilityNodeInfo nodeInfo) {
                return TextUtils.equals(nodeInfo.getViewIdResourceName(), name);
            }
        });
    }

    public static AccessibilityNodeInfo findNodeInfoInscreenByResourceName(String name, int offsetLeft, int offsetRight, int offsetTop, int offsetBottom) {
        List<AccessibilityNodeInfo> list = findNodeInfosByResourceName(getRoot(), name, true);
        for (AccessibilityNodeInfo nodeInfo : list) {
            if (centerInScreen(nodeInfo, offsetLeft, offsetRight, offsetTop, offsetBottom)) {
                return nodeInfo;
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfoByResourceName(String parentId, String name) {
        AccessibilityNodeInfo nodeInfo = findNodeInfoByViewId(parentId);
        return findNodeInfoByResourceName(nodeInfo, name);
    }

    public static void ThreadSleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isXiaoMiRom() {
        return true;
    }

    public static void toPermissionActivity(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);//设置去向意图
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }

    public static void logAllNodeInfo(AccessibilityNodeInfo parent, boolean isRect, boolean visible, String index, int left, int right, int top, int bottom) {
        try {
            if (parent == null) {
                return;
            }
            if (index == null) {
                index = "0-";
            }
            if (!visible || parent.isVisibleToUser()) {
                if (isRect) {
                    Rect rect = new Rect();
                    parent.getBoundsInScreen(rect);
                    if (allInBounds(rect, left, right, top, bottom)) {
                        LogUtil.logInfo(TAG, "logAllNodeInfo", index + " id=" + parent.getViewIdResourceName() + " text=" + parent.getText() + " visible=" + parent.isVisibleToUser() + " class=" + parent.getClassName() + " selected=" + parent.isSelected() + " rect=" + rect);
                    }
                } else {
                    LogUtil.logInfo(TAG, "logAllNodeInfo", index + " id=" + parent.getViewIdResourceName() + " text=" + parent.getText() + " visible=" + parent.isVisibleToUser() + " class=" + parent.getClassName() + " selected=" + parent.isSelected());
                }
            }
            for (int i = 0; i < parent.getChildCount(); i++) {
                logAllNodeInfo(parent.getChild(i), isRect, visible, index + i + "-", left, right, top, bottom);
            }
        } catch (Exception e) {
            LogUtil.logError(e);
        }
    }

    public static void logNodeInfoInBounds(int left, int right, int top, int bottom) {
        List<AccessibilityNodeInfo> result = findNodeInfosInBounds(left,right,top,bottom);
        for (AccessibilityNodeInfo nodeInfo : result) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            LogUtil.logInfo(TAG, "logNodeInfoInscreen", "id=" + nodeInfo.getViewIdResourceName() + " text=" + nodeInfo.getText() + " visible=" + nodeInfo.isVisibleToUser() + " class=" + nodeInfo.getClassName() + " bounds=" + rect);
        }
    }

    public static AccessibilityNodeInfo findByTextStartWith(String text) {
        List<AccessibilityNodeInfo> result = findByTextStartWith(getRoot(), text, false);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findByTextStartWith(AccessibilityNodeInfo root, final String text, boolean all) {
        return findNodeInfos(root, all, new NodeInfoComparable() {
            @Override
            public boolean compareTo(AccessibilityNodeInfo nodeInfo) {
                return !TextUtils.isEmpty(nodeInfo.getText()) && nodeInfo.getText().toString().startsWith(text);
            }
        });
    }

    public static AccessibilityNodeInfo findByContentDesc(String desc) {
        List<AccessibilityNodeInfo> result = findByContentDesc(getRoot(), desc, false);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findByContentDesc(final AccessibilityNodeInfo root, final String desc, boolean all) {
        return findNodeInfos(root, all, new NodeInfoComparable() {
            @Override
            public boolean compareTo(AccessibilityNodeInfo nodeInfo) {
                return TextUtils.equals(nodeInfo.getText(), desc);
            }
        });
    }

    public static AccessibilityNodeInfo findByTextMatch(Pattern pattern) {
        List<AccessibilityNodeInfo> result = findByTextMatch(getRoot(), pattern, false);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public static List<AccessibilityNodeInfo> findByTextMatch(AccessibilityNodeInfo root, final Pattern pattern, boolean all) {
        return findNodeInfos(root, all, new NodeInfoComparable() {
            @Override
            public boolean compareTo(AccessibilityNodeInfo nodeInfo) {
                return !TextUtils.isEmpty(nodeInfo.getText()) && pattern.matcher(nodeInfo.getText()).find();
            }
        });
    }

    private static int viewComplexity;
    public synchronized static int getViewComplexity(){
        viewComplexity = 0;
        findNodeInfos(getRoot(), true, new NodeInfoComparable() {
            @Override
            public boolean compareTo(AccessibilityNodeInfo nodeInfo) {
                viewComplexity++;
                if (nodeInfo.isVisibleToUser() && !TextUtils.isEmpty(nodeInfo.getText())) {
                    viewComplexity += 2;
                }
                return false;
            }
        });
        return viewComplexity;
    }

    public interface NodeInfoComparable {
        boolean compareTo(AccessibilityNodeInfo nodeInfo);
    }
}
