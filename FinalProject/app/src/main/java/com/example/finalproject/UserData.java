package com.example.finalproject;

import org.json.JSONObject;

import java.util.Date;

public class UserData {
    public long id;
    public int workingTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int stepCount;
    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepCount() {

        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getWorkingTime() {

        return workingTime;
    }

    public void setWorkingTime(int workingTime) {
        this.workingTime = workingTime;
    }

    public UserData(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getLong("id");
            this.workingTime = jsonObject.getInt("workingTime");
            this.stepCount = jsonObject.getInt("stepCount");
            this.date = jsonObject.getString("date");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserData() {
        workingTime = stepCount = 0;
        date = new String();
    }

    public UserData(int workingTime, int stepCount, String date) {
        this.workingTime = workingTime;
        this.stepCount = stepCount;
        this.date = date;
        this.id = 0;
    }

    public String getJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("workingTime", workingTime);
            jsonObject.put("stepCount", stepCount);
            jsonObject.put("date", date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
