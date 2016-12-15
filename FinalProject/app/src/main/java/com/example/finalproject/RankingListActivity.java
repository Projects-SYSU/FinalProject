package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RankingListActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ListView listView;
    private List<Map<String, Object>> list_datas = new LinkedList();
    private SimpleAdapter simpleAdapter;

    private Handler handler = new Handler();

    private static class mRunnable implements Runnable {
        MyAdaptor.ViewHolder viewHolder;
        int rank;

        public mRunnable(MyAdaptor.ViewHolder viewHolder, int rank) {
            this.viewHolder = viewHolder;
            this.rank = rank;
        }

        @Override
        public void run() {
            if (rank == 1) {
                viewHolder.number.setBackgroundResource(R.drawable.gold);
                viewHolder.number.setText("");
            } else if (rank == 2) {
                viewHolder.number.setBackgroundResource(R.drawable.silver);
                viewHolder.number.setText("");
            } else if (rank == 3) {
                viewHolder.number.setBackgroundResource(R.drawable.copper);
                viewHolder.number.setText("");
            } else {
                //nothing
            }

        }
    }

    public static class MyAdaptor extends BaseAdapter {
        private Context context;
        private List<Map<String, Object>> list;
        private Handler hander;

        public MyAdaptor(Context context, List<Map<String, Object>> list, Handler handler) {
            this.context = context;
            this.list = list;
            this.hander = handler;
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
                viewHolder.number = (TextView)convertView.findViewById(R.id.ranking_list_number);
                viewHolder.point = (TextView)convertView.findViewById(R.id.ranking_list_point);
                convertView.setTag(viewHolder);

            } else {
                convertView = view;
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.name.setText((String)list.get(position).get("name"));
            viewHolder.point.setText(Integer.toString((int)list.get(position).get("point")));
            viewHolder.number.setText(Integer.toString((int)list.get(position).get("rank_num")));
            viewHolder.number.setBackgroundResource(0);  //remove the background

            //set NO.1 NO.2 and NO.3 picture
            if (position == 0) {
                hander.post(new mRunnable(viewHolder, 1));
            }
            if (position == 1) {
                hander.post(new mRunnable(viewHolder, 2));
            }
            if (position == 2) {
                hander.post(new mRunnable(viewHolder, 3));
            }

            return convertView;
        }

        public static class ViewHolder {
            public TextView name;
            public TextView number;
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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nvMenu);
        listView = (ListView)findViewById(R.id.rankingList);

        setupDrawerContent(navigationView);

        init_list_data();
        sort_list_data();
        set_adaptot();
    }

    private void set_adaptot() {
        listView.setAdapter(new MyAdaptor(this, list_datas, handler));
    }

    private void sort_list_data() {
        Collections.sort(list_datas, new ComparatorValues());

        //add ranking number
        for (int i = 0; i < list_datas.size(); ++i) {
            list_datas.get(i).put("rank_num", i + 1);
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
                    default:
                        break;
                }
                return true;
            }
        });
    }
}
