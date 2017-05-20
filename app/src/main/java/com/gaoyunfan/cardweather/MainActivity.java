package com.gaoyunfan.cardweather;

import android.app.Activity;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Bean.PredictBean;
import adapter.FutureCardAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tool.AnimationUtil;
import tool.LocationUtil;
import tool.PermissionUtil;

public class MainActivity extends Activity
{

    private LocationUtil locationUtil;
    private Location newLocation;
    private String cityName;
    private List<PredictBean> preBeanList;
    private FutureCardAdapter adapter;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.floatbutton)
    FloatingActionButton fab;

    @BindView(R.id.title)
    TextView titleTxt;

    @OnClick(R.id.floatbutton)
    public void fabClick(FloatingActionButton fab)
    {
        AnimationUtil.FABRotate(fab);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideStatusBar();
        PermissionUtil.getPermission(this);
        locationUtil = new LocationUtil(this);
        final Geocoder ge = new Geocoder(this);
        getNowLocation(ge);
        preBeanList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            preBeanList.add(new PredictBean.Builder().date("2017年5月18日").
                    describe("多云").Temp("15~25").weekDay("星期四").build());
        }
        adapter = new FutureCardAdapter(this, preBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    private void getNowLocation(final Geocoder ge)
    {
        locationUtil.getLocation(new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.d("location", "获得了" + location.toString());
                newLocation = location;
                try
                {
                    cityName = ge.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getSubLocality();
                    titleTxt.setText(cityName);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {

            }
        });
    }

    /**
     * 沉浸式状态栏
     */
    private void hideStatusBar()
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


}
