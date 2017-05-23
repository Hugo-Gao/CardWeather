package com.gaoyunfan.cardweather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import Bean.JsonBean;
import Bean.TempBean;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/23.
 */

public class DetailActivity extends Activity
{
    private List<TempBean> tempBeanList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //setLayoutView(intent);
        setContentView(R.layout.detail_layout_sunny);
        JsonBean jsonBean = (JsonBean) intent.getSerializableExtra("jsonBean");
        tempBeanList = new ArrayList<>();
        getTempListFromBean(tempBeanList,jsonBean);
        ButterKnife.bind(this);
        hideStatusBar();
    }

    /**
     * 获取最高温度和最低温度集合
     * @param list
     * @param jsonBean
     * @return
     */
    private void getTempListFromBean(List<TempBean> list,JsonBean jsonBean)
    {
        for (JsonBean.Item item : jsonBean.weather)
        {
            list.add(new TempBean(Integer.parseInt(item.weatherInfo.night.get(2)), Integer.parseInt(item.weatherInfo.day.get(2))));
        }


    }

    private void setLayoutView(Intent intent)
    {
        if (intent.getStringExtra("which").equals("多云"))
        {
            setContentView(R.layout.detail_layout_sunny);

        } else
        {
            setContentView(R.layout.today_weather_layout);
        }
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
