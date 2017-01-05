package com.example.finalproject.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
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

import com.example.finalproject.services.CountDownService;
import com.example.finalproject.utilities.DynamicReceiver;
import com.example.finalproject.R;
import com.example.finalproject.services.StepService;
import com.example.finalproject.utilities.UserData;

import java.text.NumberFormat;

import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static com.example.finalproject.utilities.DynamicReceiver.COUNT_DOWN_FINISH;

public class MainActivity extends AppCompatActivity implements DynamicReceiver.DataInteraction {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView totalTime;
    private TextView minutes;
    private TextView seconds;
    private Button startBtn;
    private SeekBar seekBar;
    private ImageView photo;

    private int workingTime = 20;
    private boolean isWorking;
    private boolean isLock;
    private UserData userData = new UserData();
    private NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    private SharedPreferences sharedPreferences;
    private StepService stepService;
    private CountDownService countDownService;
    private Handler handler = new Handler();
    private DynamicReceiver dynamicReceiver = new DynamicReceiver();
    private AlertDialog.Builder alertDialog;

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
            if (countDownService.seconds > 0) {
                workingTime = countDownService.seconds;
                int s = workingTime % 60;
                int m = workingTime / 60;
                minutes.setText(numberFormat.format(m) + "");
                seconds.setText(numberFormat.format(s) + "");
                handler.postDelayed(runnable, 1000);
            }
            else {
                reset();
                userData.totalWorkingTime += workingTime;
                totalTime.setText(userData.totalWorkingTime + "分钟");

                alertDialog.setMessage("内功增加" + workingTime + "分钟").show();
            }
        }
    };

    @Override
    public void setIsLock(boolean flag) {
        this.isLock = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, StepService.class);
        bindService(intent, sc, BIND_AUTO_CREATE);
        Intent intent2 = new Intent(this, CountDownService.class);
        bindService(intent2, countDownSC, BIND_AUTO_CREATE);

        sharedPreferences = this.getSharedPreferences("temp", Context.MODE_PRIVATE);

        findViews();
        setupDrawerContent(navigationView);
        setupListeners();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SCREEN_OFF);
        intentFilter.addAction(ACTION_SCREEN_ON);
        intentFilter.addAction(COUNT_DOWN_FINISH);
        registerReceiver(dynamicReceiver, intentFilter);

        numberFormat.setMinimumIntegerDigits(2);

<<<<<<< Updated upstream:FinalProject/app/src/main/java/com/example/finalproject/activities/MainActivity.java
        alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setTitle("本次闭关结束").create();
=======
        // test web service
        CustomerClient.get(MainActivity.this, 1);
>>>>>>> Stashed changes:FinalProject/app/src/main/java/com/example/finalproject/MainActivity.java
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(dynamicReceiver);
        handler.removeCallbacks(runnable);
        unbindService(countDownSC);
        unbindService(sc);
        super.onDestroy();
    }

    private void findViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nvMenu);
        totalTime = (TextView) findViewById(R.id.totalTime);
        minutes = (TextView) findViewById(R.id.minutes);
        seconds = (TextView) findViewById(R.id.seconds);
        startBtn = (Button) findViewById(R.id.startBtn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        photo = (ImageView) findViewById(R.id.photo);
    }

    private void setupListeners() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    workingTime = progress / 5 * 5 + 20;
                    minutes.setText(workingTime + "");
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
                    workingTime *= 60;
                    startBtn.setText("放弃");
                    countDownService.startCountingDown(workingTime);
                    handler.post(runnable);
                } else {
                    countDownService.cancelCountingDown();
                    reset();
                }
            }
        });
        dynamicReceiver.setDataListener(this);
    }

    private void reset() {
        isWorking = false;
        handler.removeCallbacks(runnable);
        seekBar.setVisibility(View.VISIBLE);
        workingTime = seekBar.getProgress() / 5 * 5 + 20;
        minutes.setText(workingTime + "");
        seconds.setText("00");
        startBtn.setText("闭关");
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