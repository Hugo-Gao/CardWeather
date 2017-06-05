package com.gaoyunfan.cardweather;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Bean.JsonBean;
import Bean.TempBean;
import MyView.ChartView;
import MyView.CircleView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/23.
 */

public class DetailActivity extends Activity
{

    private List<TempBean> tempBeanList;

    @BindView(R.id.chart_view)
    ChartView chartView;

    @BindView(R.id.temp_in_pic)
    TextView tempInPic;

    @BindView(R.id.describe_in_pic)
    TextView descibeInPic;

    @BindView(R.id.windCircle)
    CircleView windCircleView;

    @BindView(R.id.airCircle)
    CircleView airCircleView;

    @BindView(R.id.air_describe)
    TextView air_des;

    @BindView(R.id.wind_describe)
    TextView wind_des;

    @BindView(R.id.air_suggest)
    TextView airSgt;

    @BindView(R.id.air_condition_des)
    TextView airConditionDes;
    @BindView(R.id.air_condition_sug)
    TextView airConditionSug;
    @BindView(R.id.sport_des)
    TextView sportDes;
    @BindView(R.id.sport_sug)
    TextView sportSug;
    @BindView(R.id.rays_des)
    TextView raysDes;
    @BindView(R.id.rays_sug)
    TextView raysSug;
    @BindView(R.id.sick_des)
    TextView sickDes;
    @BindView(R.id.sick_sug)
    TextView sickSug;
    @BindView(R.id.wash_car_des)
    TextView washCarDes;
    @BindView(R.id.wash_car_sug)
    TextView washCarSug;
    @BindView(R.id.wear_des)
    TextView wearDes;
    @BindView(R.id.wear_sug)
    TextView wearSug;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setLayoutView(intent);
        ButterKnife.bind(this);
        JsonBean jsonBean = (JsonBean) intent.getSerializableExtra("jsonBean");
        tempBeanList = new ArrayList<>();
        getTempListFromBean(tempBeanList, jsonBean);
        hideStatusBar();
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/Pacifico.ttf");//根据路径得到Typeface
        tempInPic.setText(jsonBean.realTime.weatherInfo.temperature + "°");
        tempInPic.setTypeface(tf);
        descibeInPic.setTypeface(tf);
        initialView(jsonBean);
        initialSug(jsonBean);

    }

    private void initialSug(JsonBean jsonBean)
    {
        airConditionDes.setText("空调建议:" + jsonBean.lifeInfo.suggesstInfo.kongtiao.get(0));
        airConditionSug.setText(jsonBean.lifeInfo.suggesstInfo.kongtiao.get(1));

        sportDes.setText("运动建议:" + jsonBean.lifeInfo.suggesstInfo.yundong.get(0));
        sportSug.setText(jsonBean.lifeInfo.suggesstInfo.yundong.get(1));

        raysDes.setText("紫外线强度:" + jsonBean.lifeInfo.suggesstInfo.ziwaixian.get(0));
        raysSug.setText(jsonBean.lifeInfo.suggesstInfo.ziwaixian.get(1));

        sickDes.setText("感冒状况:" + jsonBean.lifeInfo.suggesstInfo.ganmao.get(0));
        sickSug.setText(jsonBean.lifeInfo.suggesstInfo.ganmao.get(1));

        washCarDes.setText("洗车建议:" + jsonBean.lifeInfo.suggesstInfo.xiche.get(0));
        washCarSug.setText(jsonBean.lifeInfo.suggesstInfo.xiche.get(1));

        wearDes.setText("穿衣建议:" + jsonBean.lifeInfo.suggesstInfo.chuanyi.get(0));
        wearSug.setText(jsonBean.lifeInfo.suggesstInfo.chuanyi.get(1));
    }


    private void initialView(JsonBean jsonBean)
    {
        initCircle(jsonBean);
        initCharView(chartView, tempBeanList, jsonBean);
    }

    private void initCircle(JsonBean jsonBean)
    {
        windCircleView.setType(CircleView.type.WindSpeed);
        windCircleView.setCurValue(Float.parseFloat(jsonBean.realTime.windInfo.power.substring(0, 1)));
        windCircleView.fresh();
        wind_des.setText("风力描述:" + judgeWindPower(windCircleView.getCurValue()));

        airCircleView.setType(CircleView.type.AQI);
        if (jsonBean.PM25 != null)
        {
            airCircleView.setCurValue(Float.parseFloat(jsonBean.PM25.info.curPm));
            air_des.setText("空气质量:" + jsonBean.PM25.info.quality);
            airSgt.setText(jsonBean.PM25.info.des);
        }
        airCircleView.fresh();
    }

    private String judgeWindPower(float curValue)
    {
        switch ((int) curValue)
        {
            case 0:
                return "烟直上";
            case 1:
                return "烟示风向";
            case 2:
                return "感觉有风";
            case 3:
                return "旌旗展开";
            case 4:
                return "吹起尘土";
            case 5:
                return "小树摇摆";
            case 6:
                return "电线有声";
            case 7:
                return "步行困难";
            case 8:
                return "折毁树枝";
            case 9:
                return "小损房屋";
            case 10:
                return "拔起树木";
            case 11:
                return "损毁普遍";
            case 12:
                return "摧毁巨大";
            default:
                return "";
        }

    }

    private void initCharView(ChartView chartView, List<TempBean> tempBeanList, JsonBean jsonBean)
    {
        chartView.setTitle("七日最高温度折线图");
        chartView.setxLabel(jsonBean);
        Log.d("temp", tempBeanList.toString());
        chartView.setData(tempBeanList);
        chartView.fresh();
    }

    /**
     * 获取最高温度和最低温度集合
     *
     * @param list
     * @param jsonBean
     * @return
     */
    private void getTempListFromBean(List<TempBean> list, JsonBean jsonBean)
    {
        for (JsonBean.Item item : jsonBean.weather)
        {
            list.add(new TempBean(Integer.parseInt(item.weatherInfo.night.get(2)), Integer.parseInt(item.weatherInfo.day.get(2))));
        }


    }

    private void setLayoutView(Intent intent)
    {
        if (intent.getStringExtra("which").contains("多云") || intent.getStringExtra("which").contains("阴"))
        {
            setContentView(R.layout.detail_layout_cloudy);
            return;
        } else if (intent.getStringExtra("which").contains("雨"))
        {
            setContentView(R.layout.detail_layout_rainy);
            return;
        } else if (intent.getStringExtra("which").contains("雪"))
        {
            setContentView(R.layout.detail_layout_snowy);
            return;
        } else
        {
            setContentView(R.layout.detail_layout_sunny);
            return;
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
