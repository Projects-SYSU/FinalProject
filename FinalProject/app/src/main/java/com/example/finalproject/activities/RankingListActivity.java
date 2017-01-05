package com.example.finalproject.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.finalproject.CustomerClient;
import com.example.finalproject.R;
import com.example.finalproject.UserData;
import com.example.finalproject.utilities.RefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class RankingListActivity extends AppCompatActivity implements RefreshListView.IRefreshListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<Map<String, Object>> list_datas = new LinkedList();
    private Map<String, Object> ME = new HashMap<>();
//    private TextView myrank;
//    private TextView mypoint;
    private MyAdaptor myAdaptor;
    private RefreshListView refreshListView;

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update_data();
            }
        }, 2000);
    }

    public class MyAdaptor extends BaseAdapter {
        private Context context;
        private List<Map<String, Object>> list;
        private Map<String, Object> me;

        public MyAdaptor(Context context, List<Map<String, Object>> list, Map<String, Object> me) {
            this.context = context;
            this.list = list;
            this.me = me;
        }

        @Override
        public int getCount() {
            if (list == null) {
                return  0;
            }
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            if (list == null) {
                return null;
            }
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            View convertView;
            ViewHolder viewHolder;

            if (view == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.rankinglistitem, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView)convertView.findViewById(R.id.ranking_list_name);
                viewHolder.rank = (TextView)convertView.findViewById(R.id.ranking_list_rank);
                viewHolder.point = (TextView)convertView.findViewById(R.id.ranking_list_point);
                convertView.setTag(viewHolder);

            } else {
                convertView = view;
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.name.setText((String)list.get(position).get("name"));
            viewHolder.point.setText(Integer.toString((int)list.get(position).get("point")));
            viewHolder.rank.setText(Integer.toString((int)list.get(position).get("rank_num")));
            viewHolder.rank.setBackgroundResource(0);  //remove the background
            convertView.setBackgroundColor(ContextCompat.getColor(RankingListActivity.this, R.color.white));

            if (list.get(position).get("name").equals(me.get("name"))) {
                convertView.setBackgroundColor(ContextCompat.getColor(RankingListActivity.this, R.color.bright_gray));
            }

            //set NO.1 NO.2 and NO.3 picture
            if (position == 0) {
                //hander.post(new mRunnable(viewHolder, 1));
                viewHolder.rank.setBackgroundResource(R.drawable.gold);
                viewHolder.rank.setText("");
            }
            if (position == 1) {
                viewHolder.rank.setBackgroundResource(R.drawable.silver);
                viewHolder.rank.setText("");
            }
            if (position == 2) {
                viewHolder.rank.setBackgroundResource(R.drawable.copper);
                viewHolder.rank.setText("");
            }
            return convertView;
        }

        public class ViewHolder {
            public TextView name;
            public TextView rank;
            public TextView point;
        }
    }

    public static final class ComparatorValues implements Comparator<Map<String, Object>> {

        @Override
        public int compare(Map<String, Object> object1, Map<String, Object> object2) {
            if((int)object1.get("point") < (int)object2.get("point")) {
                return 1;
            } else if ((int)object1.get("point") > (int)object2.get("point")) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_list);

        findView();

        setupDrawerContent(navigationView);

        set_mydata();

        init_list_data();
        sort_list_data();
        myAdaptor = new MyAdaptor(this, list_datas, ME);
        refreshListView.setAdapter(myAdaptor);
        refreshListView.setInterface(RankingListActivity.this);
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //set dialoglayout
                RelativeLayout rl_dia = (RelativeLayout)LayoutInflater.from(RankingListActivity.this)
                        .inflate(R.layout.rankinglist_dialog, null);
                TextView nei = (TextView)rl_dia.findViewById(R.id.rl_dialog_nei);
                TextView qing = (TextView)rl_dia.findViewById(R.id.rl_dialog_qing);
                nei.setText(list_datas.get(position).get("nei").toString() + "分");
                qing.setText(list_datas.get(position).get("qing").toString() + "步");

                new AlertDialog.Builder(RankingListActivity.this)
                        .setTitle(list_datas.get(position).get("name").toString())
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .setView(rl_dia)
                        .show();
            }
        });
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

    private void findView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nvMenu);
        //listView = (ListView)findViewById(R.id.rankingList);
        refreshListView = (RefreshListView)findViewById(R.id.rankingList);
//        myrank = (TextView)findViewById(R.id.ranking_list_myrank);
//        mypoint = (TextView)findViewById(R.id.ranking_list_mypoint);
    }

    private void set_mydata() {
        ME.put("name", "N5");
        ME.put("point", 5005);
        ME.put("qing", 20005);
        ME.put("nei", 105);

//        mypoint.setText(String.valueOf(5005));
    }

    private void update_data() {
        get_data(); //from server? now nothing
        sort_list_data();
        myAdaptor.notifyDataSetChanged();
        refreshListView.refreshComplete();
    }

    private void get_data() {
        //list_datas.get(0).put("point", (int)list_datas.get(0).get("point") - 10);
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    List<UserData> users = new ArrayList<UserData>();
                    for (int i = 0; i < response.length(); i++) {
                        users.add(new UserData(response.getJSONObject(i)));
                        //Log.v("Test ", users.get(i).getName());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        CustomerClient.getTotalCustomer(RankingListActivity.this, handler);
    }

    private void sort_list_data() {
        Collections.sort(list_datas, new ComparatorValues());

        //add ranking number
        for (int i = 0; i < list_datas.size(); ++i) {
            list_datas.get(i).put("rank_num", i + 1);
            if (list_datas.get(i).get("name").toString().equals(ME.get("name"))) {
//                myrank.setText(String.valueOf(i + 1));
            }
        }
    }

    private void init_list_data() {
        int data_num = 10;
        for (int i = 0; i < data_num; ++i) {
            Map<String, Object> t = new HashMap<>();
            t.put("name", String.format("N%d", i));
            t.put("nei", 100 + i);
            t.put("qing", 20000 + i);
            t.put("point", 5000 + i);
            list_datas.add(t);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(RankingListActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.rankingList:
                        item.setChecked(false);
                        break;
                    case R.id.steps:
                        intent = new Intent(RankingListActivity.this, CounterActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:
                        intent = new Intent(RankingListActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.history:
                        intent = new Intent(RankingListActivity.this, HistoryActivity.class);
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
