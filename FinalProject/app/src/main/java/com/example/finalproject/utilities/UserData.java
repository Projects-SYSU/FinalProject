package com.example.finalproject.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shower on 2016/12/22 0022.
 */

public class UserData {
    public int totalWorkingTime;
    public int stepCount;
    public Date date;

    public UserData() {
        refresh();
    }

    public UserData(String date, int totalWorkingTime, int stepCount) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD");
        this.date = simpleDateFormat.parse(date);
        this.totalWorkingTime = totalWorkingTime;
        this.stepCount = stepCount;
    }

    private void refresh() {
        totalWorkingTime = stepCount = 0;
        date = new Date();
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD");
        return simpleDateFormat.format(date);
    }
}
