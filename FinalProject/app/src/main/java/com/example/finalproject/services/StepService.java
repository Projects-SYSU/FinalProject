package com.example.finalproject.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import com.example.finalproject.utilities.StepDetector;

public class StepService extends Service {
    private final IBinder binder = new MyBinder();
    private SensorManager sensorManager;
    private StepDetector stepDetector;

    @Override
    public void onCreate() {
        super.onCreate();
        stepDetector = new StepDetector(this);
        sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        sensorManager.unregisterListener(stepDetector);
//    }

    public int getSteps() {
        return stepDetector.CURRENT_STPEPS;
    }

    public class MyBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
