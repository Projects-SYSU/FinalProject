package com.example.finalproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.utilities.StepDetector;

public class CounterActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView steps;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nvMenu);
        steps = (TextView)findViewById(R.id.steps);
        setupDrawerContent(navigationView);

        myHandler = new MyHandler();
        MyThread myThread = new MyThread();
        new Thread(myThread).start();
    }

    @Override
    protected void onStart() {
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.name);
        SharedPreferences sharedPreferences = getSharedPreferences("tempData", MODE_PRIVATE);
        String n = sharedPreferences.getString("name", "大侠");
        name.setText(n);
        super.onStart();
    }

    class MyHandler extends Handler {
        public MyHandler() { }

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            steps.setText(String.valueOf(StepDetector.CURRENT_STPEPS));
        }
    }

    class MyThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                Message message = new Message();
                myHandler.sendMessage(message);
                try{
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.home:
                        intent = new Intent(CounterActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.rankingList:
                        intent = new Intent(CounterActivity.this, RankingListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.steps:
                        item.setChecked(false);
                        break;
                    case R.id.settings:
                        intent = new Intent(CounterActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.history:
                        intent = new Intent(CounterActivity.this, HistoryActivity.class);
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
