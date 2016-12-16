package com.example.finalproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView minutesView;
    private TextView clock;
    private Button startBtn;
    private SeekBar seekBar;
    private ImageView photo;
    private int min;
    private boolean flag;
    private CountDownTimer countDownTimer;
    private NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    private SharedPreferences sharedPreferences;
    private StepService stepService;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, StepService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);

        numberFormat.setMinimumIntegerDigits(2);
        sharedPreferences = this.getSharedPreferences("info", Context.MODE_PRIVATE);

        findViews();
        setupDrawerContent(navigationView);
        setupListeners();
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
                if (!flag) {
                    flag = true;
                    seekBar.setVisibility(View.INVISIBLE);
                    if (min > 60) {
                        photo.setImageResource(R.mipmap.happy);
                    }
                    else {
                        photo.setImageResource(R.mipmap.smile);
                    }
                    min *= 60;
                    startBtn.setText("放弃");

                    countDownTimer = new CountDownTimer(min * 60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (min > 0) {
                                min--;
                                int s = min % 60;
                                int m = min / 60;
                                String temp = m + ":" + numberFormat.format(s);
                                clock.setText(temp);
                            }
                        }

                        @Override
                        public void onFinish() {
                            reset();
                            flag = false;
                        }
                    };
                    countDownTimer.start();
                } else {
                    failed();
                }
            }
        });
    }

    private void failed() {
        flag = false;
        reset();
        photo.setImageResource(R.mipmap.unhappy);
        countDownTimer.cancel();
    }

    private void reset() {
        seekBar.setVisibility(View.VISIBLE);
        startBtn.setText("闭关");
        min = seekBar.getProgress() / 5 * 5 + 20;
        clock.setText(min + ":00");
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (flag) {
            failed();
            editor.putBoolean("flag", true);
        } else {
            editor.putBoolean("flag", false);
        }
        editor.putInt("min", min);
        editor.commit();
        super.onStop();
    }

    @Override
    protected void onStart() {
        min = sharedPreferences.getInt("min", 20);
        flag = sharedPreferences.getBoolean("flag", false);
        if (flag) {
            photo.setImageResource(R.mipmap.unhappy);
            seekBar.setProgress(min - 20);
            flag = false;
        }
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