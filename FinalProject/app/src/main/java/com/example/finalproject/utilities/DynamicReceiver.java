package com.example.finalproject.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.finalproject.R;
import com.example.finalproject.activities.MainActivity;

import static android.app.Notification.DEFAULT_ALL;
import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static android.content.Intent.ACTION_TIME_TICK;

/**
 * Created by Shower on 2016/12/18 0018.
 */

public class DynamicReceiver extends BroadcastReceiver {
    private DataInteraction dataInteraction;
    public static final String COUNT_DOWN_FINISH = "com.example.finalproject.finish";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("tag", intent.getAction());
        if (intent.getAction().equals(ACTION_SCREEN_OFF)) {
            dataInteraction.setIsLock(true);
        } else if (intent.getAction().equals(COUNT_DOWN_FINISH)) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("闭关结束")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("闭关结束")
                    .setContentText("闭关结束!!")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setDefaults(DEFAULT_ALL);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = builder.build();
            manager.notify(0, notify);
        } else if (intent.getAction().equals(ACTION_TIME_TICK)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("tempData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("steps", StepDetector.CURRENT_STPEPS);
            editor.commit();
        }
    }

    public interface DataInteraction {
        void setIsLock(boolean flag);
    }

    public void setDataListener(DataInteraction dataInteraction) {
        this.dataInteraction = dataInteraction;
    }
}
