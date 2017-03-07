package com.southernbox.swipemenulayout.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.southernbox.swipemenulayout.R;
import com.southernbox.swipemenulayout.adapter.MainAdapter;
import com.southernbox.swipemenulayout.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;
    private List<Entity> mList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private TextView tvEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MainAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void initData() {
        mList.clear();
        mList.addAll(getData());
        mAdapter.notifyDataSetChanged();
    }

    private List<Entity> getData() {
        List<Entity> list = new ArrayList<>();
        list.add(new Entity("加内特"));
        list.add(new Entity("韦德"));
        list.add(new Entity("詹姆斯"));
        list.add(new Entity("安东尼"));
        list.add(new Entity("科比"));
        list.add(new Entity("乔丹"));
        list.add(new Entity("奥尼尔"));
        list.add(new Entity("麦格雷迪"));
        list.add(new Entity("艾弗森"));
        list.add(new Entity("哈达威"));
        list.add(new Entity("纳什"));
        list.add(new Entity("弗朗西斯"));
        list.add(new Entity("姚明"));
        list.add(new Entity("库里"));
        list.add(new Entity("邓肯"));
        list.add(new Entity("吉诺比利"));
        list.add(new Entity("帕克"));
        list.add(new Entity("杜兰特"));
        list.add(new Entity("韦伯"));
        list.add(new Entity("威斯布鲁克"));
        list.add(new Entity("霍华德"));
        list.add(new Entity("保罗"));
        list.add(new Entity("罗斯"));
        list.add(new Entity("加索尔"));
        list.add(new Entity("隆多"));
        list.add(new Entity("诺维斯基"));
        list.add(new Entity("格里芬"));
        list.add(new Entity("波什"));
        list.add(new Entity("伊戈达拉"));
        return list;
    }
}
