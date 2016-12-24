package com.example.finalproject.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.finalproject.services.StepService;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

/**
 * Created by Shower on 2016/12/24 0024.
 */

public class StaticReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("boot", intent.getAction());
        if (intent.getAction().equals(ACTION_BOOT_COMPLETED)) {
            Intent intentService = new Intent(context, StepService.class);
            context.startService(intentService);
        }
    }
}
