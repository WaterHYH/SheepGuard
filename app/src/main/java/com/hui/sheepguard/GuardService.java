package com.hui.sheepguard;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.dodoo_tech.gfal.utils.LogUtil;

public class GuardService extends Service {
    private static final String TAG = "GuardService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final String CHANNEL_ID = "520";
    public static final int NOTIFICATION_ID = 1314;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.logInfo(TAG,"onStartCommand");
        // 创建一个Notification并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setAutoCancel(false)//通知设置不会自动显示
                .setSmallIcon(R.mipmap.ic_launcher)//设置通知的小图标
                .setContentTitle("绵羊卫士")
                .setContentText("运行中，不要关闭");//设置通知的内容

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;// 设置为默认的声音
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_NO_CLEAR;

        // 显示通知
        startForeground(NOTIFICATION_ID,notification);

        return super.onStartCommand(intent, flags, startId);
    }
}
