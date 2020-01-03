package com.hui.sheepguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dodoo_tech.gfal.utils.LogUtil;

/**
 * @author Created by waterHYH on 2020-01-03.
 */
public class GuardReceiver extends BroadcastReceiver {
    public static final String TAG = "GuardReceiver";
    public static final String SHEEP_ACTION = GuardHandle.SHEEP_PACKAGE+".action.beat";
    private static BroadcastReceiver sheepReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        //LogUtil.logInfo(TAG,"onReceive","action="+intent.getAction());
        GuardHandle.setSheepBeatTime(System.currentTimeMillis());
    }

    public static void registerReceiver(Context context) {
        if (sheepReceiver == null) {
            try{
                sheepReceiver = new GuardReceiver();
                IntentFilter filter = new IntentFilter(SHEEP_ACTION);
                filter.setPriority(Integer.MAX_VALUE);
                context.registerReceiver(sheepReceiver, filter);
                LogUtil.logInfo(TAG + "->registerReceiver");
            }catch (Exception e){
                LogUtil.logError(e);
            }
        }
    }
    public static void unregisterReceiver(Context context) {
        if (sheepReceiver != null){
            try {
                context.unregisterReceiver(sheepReceiver);
                LogUtil.logInfo(TAG + "->unregisterReceiver");
            } catch (Exception e) {
                e.printStackTrace();
            }
            sheepReceiver = null;
        }
    }
}
