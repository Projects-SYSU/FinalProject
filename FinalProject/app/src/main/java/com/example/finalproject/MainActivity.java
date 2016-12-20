package com.example.finalproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

import static android.content.Intent.ACTION_SCREEN_OFF;

public class MainActivity extends AppCompatActivity {
    static public boolean isLock;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView minutesView;
    private TextView clock;
    private Button startBtn;
    private SeekBar seekBar;
    private ImageView photo;
    private int min;
    private boolean isWorking;
    private NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    private SharedPreferences sharedPreferences;
    private StepService stepService;
    private CountDownService countDownService;
    private Handler handler = new Handler();
    private LockScreenReceiver lockScreenReceiver = new LockScreenReceiver();
    private PowerManager.WakeLock wakeLock;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            stepService = ((StepService.MyBinder)iBinder).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            stepService = null;
        }
    };

    private ServiceConnection countDownSC = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            countDownService = ((CountDownService.Mybinder)iBinder).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            countDownService = null;
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (countDownService.min > 0) {
                min = countDownService.min;
                int s = min % 60;
                int m = min / 60;
                String temp = m + ":" + numberFormat.format(s);
                clock.setText(temp);
                handler.postDelayed(runnable, 1000);
            }
            else {
                reset();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, StepService.class);
//        bindService(intent, sc, BIND_AUTO_CREATE);

        Intent intent2 = new Intent(this, CountDownService.class);
        bindService(intent2, countDownSC, BIND_AUTO_CREATE);

        numberFormat.setMinimumIntegerDigits(2);
        sharedPreferences = this.getSharedPreferences("info", Context.MODE_PRIVATE);

        findViews();
        setupDrawerContent(navigationView);
        setupListeners();

        IntentFilter intentFilter = new IntentFilter(ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, intentFilter);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakeLock.acquire();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(lockScreenReceiver);
        handler.removeCallbacks(runnable);
        unbindService(countDownSC);
        wakeLock.release();
        super.onDestroy();
    }

    private void findViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nvMenu);
        minutesView = (TextView) findViewById(R.id.minutes);
        clock = (TextView) findViewById(R.id.clock);
        startBtn = (Button) findViewById(R.id.startBtn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        photo = (ImageView) findViewById(R.id.photo);
    }

    private void setupListeners() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    min = progress / 5 * 5 + 20;
                    clock.setText(min + ":00");
                    if (min > 60) {
                        photo.setImageResource(R.mipmap.happy);
                    }
                    else {
                        photo.setImageResource(R.mipmap.smile);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorking) {
                    isWorking = true;
                    seekBar.setVisibility(View.INVISIBLE);
                    if (min > 60) {
                        photo.setImageResource(R.mipmap.happy);
                    }
                    else {
                        photo.setImageResource(R.mipmap.smile);
                    }
                    min *= 60;
                    startBtn.setText("放弃");

                    countDownService.startCountingDown(min);
                    handler.post(runnable);
                } else {
                    failed();
                }
            }
        });
    }

    private void failed() {
        isWorking = false;
        handler.removeCallbacks(runnable);
        reset();
        photo.setImageResource(R.mipmap.unhappy);
        countDownService.cancelCountingDown();
    }

    private void reset() {
        seekBar.setVisibility(View.VISIBLE);
        startBtn.setText("闭关");
        min = seekBar.getProgress() / 5 * 5 + 20;
        clock.setText(min + ":00");
    }

    @Override
    protected void onStop() {
        Log.d("islock", isLock + "");
        if (!isLock) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isWorking) {
                failed();
                editor.putBoolean("isWorking", true);
            } else {
                editor.putBoolean("isWorking", false);
            }
            editor.putInt("min", min);
            editor.commit();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        if (!isLock) {
            min = sharedPreferences.getInt("min", 20);
            isWorking = sharedPreferences.getBoolean("isWorking", false);
            if (isWorking) {
                photo.setImageResource(R.mipmap.unhappy);
                seekBar.setProgress(min - 20);
                isWorking = false;
            }
        }
        isLock = false;
        super.onStart();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.home:
                        item.setChecked(false);
                        break;
                    case R.id.rankingList:
                        Intent intent = new Intent(MainActivity.this, RankingListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.steps:
                        intent = new Intent(MainActivity.this, CounterActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}