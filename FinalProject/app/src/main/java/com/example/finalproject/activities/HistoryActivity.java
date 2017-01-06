package com.example.finalproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.utilities.DBHelper;
import com.example.finalproject.utilities.UserData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class HistoryActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LineChart workingChart;
    private LineChart stepsChart;
    private final String[] xDates = new String[7];
    private List<Integer> workingTime = new ArrayList<>();
    private List<Integer> steps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        findView();
        setupDrawerContent(navigationView);
    }

    @Override
    protected void onStart() {
        initX();
        setChartStyle(workingChart, prepareLineData("内功"));
        setChartStyle(stepsChart, prepareLineData("轻功"));

        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.name);
        SharedPreferences sharedPreferences = getSharedPreferences("tempData", MODE_PRIVATE);
        String n = sharedPreferences.getString("name", "大侠");
        name.setText(n);
        super.onStart();
    }

    private void initX() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d");
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        for (int i = 6; i >= 0; --i) {
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date = calendar.getTime();
            xDates[i] = simpleDateFormat.format(date);
        }
        simpleDateFormat = new SimpleDateFormat("M");
        xDates[0] = simpleDateFormat.format(date) + "月" + xDates[0];
    }

    private void findView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nvMenu);
        workingChart = (LineChart) findViewById(R.id.lineChart_working);
        stepsChart = (LineChart) findViewById(R.id.lineChart_steps);
    }

    private void getData() throws ParseException {
        DBHelper dbHelper = new DBHelper(HistoryActivity.this);
        List<UserData> userData = dbHelper.getHistory();
        for (int i = userData.size() - 1, k = 0; i >= 0 && k < 7; --i) {
            workingTime.add(0, userData.get(i).totalWorkingTime);
            steps.add(0, userData.get(i).stepCount);
            k++;
        }
    }

    private void setChartStyle(LineChart lineChart, LineData lineData) {
        lineChart.setData(lineData);
        lineChart.setScaleEnabled(false);
        lineChart.setDragEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawBorders(false);
        lineChart.setTouchEnabled(false);
        lineChart.setBackgroundColor(Color.GRAY);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getLegend().setForm(Legend.LegendForm.CIRCLE);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return xDates[(int)v - 1];
            }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
    }

    private LineData prepareLineData(String s) {
        List<Entry> entries = new ArrayList<>();

        for (int i = 1; i <= 7; ++i) {
            entries.add(new Entry(i, (float) (10000 * Math.random())));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, s);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setLineWidth(3.0f);
        lineDataSet.setValueTextSize(12.0f);
        lineDataSet.setCircleRadius(5.0f);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        return new LineData(dataSets);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                Intent intent;
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.home:
                        intent = new Intent(HistoryActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.rankingList:
                        intent = new Intent(HistoryActivity.this, RankingListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.steps:
                        intent = new Intent(HistoryActivity.this, CounterActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent = new Intent(HistoryActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.history:
                        item.setChecked(false);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
