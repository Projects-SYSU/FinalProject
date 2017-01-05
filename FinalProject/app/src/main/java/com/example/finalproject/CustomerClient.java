package com.example.finalproject;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by parda on 2016/12/23.
 */

public class CustomerClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static UserData currentUser = null;

    private static final String url = "http://60.205.216.158:8080/service1/customerservice/customers/";

    public static void get(Context context, long id) {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        client.get(context, url + id, headers.toArray(new Header[headers.size()]), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    currentUser = new UserData(response);
                    Log.v("Test", currentUser.date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getTotalCustomer(Context context, JsonHttpResponseHandler handler) {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        client.get(context, url + "tot", headers.toArray(new Header[headers.size()]), null, handler);
    }

    public static void post(Context context, UserData userData) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(userData.getJSON());
            Log.v("Test", userData.getJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (entity == null) {
            Log.v("ERR", "failed at post");
            return;
        }

        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.post(context, url + "add", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    currentUser = new UserData(response);
                    Log.v("Test", currentUser.date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void updateData(Context context) {
        if (currentUser == null)
            return;
        StringEntity entity = null;
        try {
            entity = new StringEntity(currentUser.getJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (entity == null) {
            Log.v("ERR", "failed at post");
            return;
        }

        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.post(context, url + "update", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v("update", currentUser.getJSON());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v("update", "failed");
            }
        });
    }
}
