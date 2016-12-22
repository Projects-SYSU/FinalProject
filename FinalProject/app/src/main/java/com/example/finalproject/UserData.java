package com.example.finalproject;

import java.util.Date;

/**
 * Created by Shower on 2016/12/22 0022.
 */

public class UserData {
    public int workingTime;
    public int stepCount;
    public Date date;

    public UserData() {
        workingTime = stepCount = 0;
        date = new Date();
    }
}
