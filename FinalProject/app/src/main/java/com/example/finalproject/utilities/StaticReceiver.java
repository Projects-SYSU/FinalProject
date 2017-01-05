package com.example.finalproject.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.finalproject.services.StepService;

import java.text.ParseException;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.content.Intent.ACTION_DATE_CHANGED;

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
        } else if (intent.getAction().equals(ACTION_DATE_CHANGED)) {
//            SharedPreferences sharedPreferences = context.getSharedPreferences("tempData", Context.MODE_PRIVATE);
//            int totalWorkingTime = sharedPreferences.getInt("minutes", 0);
//            int totalSteps = sharedPreferences.getInt("steps", 0);
//            String date = sharedPreferences.getString("today", "2000-01-01");
//            DBHelper helper = new DBHelper(context);
//            try {
//                helper.update(new UserData(date, totalWorkingTime, totalSteps));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt("minutes", 0);
//            editor.putInt("steps", 0);
//            editor.commit();
        }
    }
}
