package com.example.finalproject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Shower on 2016/12/20 0020.
 */

public class CountDownService extends Service {
    public int min;
    private PowerManager.WakeLock wakeLock;
    private CountDownTimer countDownTimer;
    private final IBinder binder = new Mybinder();

    public class Mybinder extends Binder {
        CountDownService getService() {
            return CountDownService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startCountingDown(int minutes) {
        min = minutes;
        min = 10;
        countDownTimer = new CountDownTimer(min * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (min > 0) {
                    min--;
                    Log.d("min", min + "");
                }
            }

            @Override
            public void onFinish() {
                min = 0;
            }
        };
        countDownTimer.start();
    }

    public void cancelCountingDown() {
        countDownTimer.cancel();
    }

    @Override
    public void onCreate() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakeLock.acquire();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        super.onDestroy();
    }
}
