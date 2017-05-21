package com.gaoyunfan.cardweather;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Bean.JsonBean;
import Bean.MainBean;
import Bean.PredictBean;
import adapter.FutureCardAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    private final String AppKey = "5525d200e35c443eb70948bc960141b3";
    private final String apiUri = "http://api.avatardata.cn/Weather/Query";
    private Gson gson;
    private final int GET_JSON = 1;
    private final int REMOVE_ALL = 2;
    private final int ADD_PRE_BEAN = 3;

    private Handler handler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case GET_JSON:
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    if (jsonBean != null)
                    {
                        MainBean bean = converToMainBean(jsonBean);
                        preBeanList = convertToPreBeanList(jsonBean);
                        reFreshTopCard(bean);
                        reFreshPreCard(preBeanList);
                    }
                    break;
                case REMOVE_ALL:
                    adapter.removeAll();
                    break;
                case ADD_PRE_BEAN:
                    int index=msg.arg1;
                    List<PredictBean> list = (List<PredictBean>) msg.obj;
                    adapter.addItem(list.get(index));
                    if (index == list.size() - 1)
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    break;
            }
            return false;
        }
    });




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

    @BindView(R.id.weekday)
    TextView todayWeekDay;

    @BindView(R.id.temperature)
    TextView todayTemp;

    @BindView(R.id.weather_logo)
    ImageView todayLogo;

    @BindView(R.id.update_time)
    TextView todayUpdateTime;

    @BindView(R.id.pm25_txt)
    TextView todayPM25;

    @BindView(R.id.humidity_txt)
    TextView todayHumidity;

    @BindView(R.id.windspeed_txt)
    TextView todayWindSpeed;

    @Nullable
    @BindView(R.id.future_time_txt)
    TextView futureTimeTxt;

    @Nullable
    @BindView(R.id.future_date_txt)
    TextView futureDateTxt;

    @Nullable
    @BindView(R.id.future_pic)
    ImageView futureImg;

    @Nullable
    @BindView(R.id.future_temp_txt)
    TextView futureTempTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideStatusBar();
        PermissionUtil.getPermission(this);
        preBeanList = new ArrayList<>();
        locationUtil = new LocationUtil(this);
        adapter = new FutureCardAdapter(this, preBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DefaultItemAnimator animator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(animator);
        recyclerView.getItemAnimator().setAddDuration(500);
        recyclerView.setAdapter(adapter);
        final Geocoder ge = new Geocoder(this);
        getNowLocation(ge);
        gson = new Gson();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getNewWeatherInfo();
            }
        });

    }

    private void getNewWeatherInfo()
    {
        OkHttpClient mClient = new OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS).
                writeTimeout(2, TimeUnit.SECONDS)
                .connectTimeout(2, TimeUnit.SECONDS).build();

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("key", AppKey);
        formBuilder.add("cityname", cityName);
        final Request request = new Request.Builder().url(apiUri).post(formBuilder.build()).build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.d("haha", "获取数据失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String jsonString = response.body().string();
                try
                {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (jsonObject.get("reason").equals("Succes"))
                    {
                        Log.d("haha", "获取到正确json");
                        preBeanList.clear();
                        Message message2 = Message.obtain();
                        message2.what = REMOVE_ALL;
                        handler.sendMessage(message2);
                        JSONObject resultobject = jsonObject.getJSONObject("result");
                        JsonBean jsonBean = gson.fromJson(String.valueOf(resultobject), JsonBean.class);

                        Message message = Message.obtain();
                        message.what = GET_JSON;
                        message.obj = jsonBean;
                        handler.sendMessage(message);
                    } else
                    {
                        Log.d("haha", "服务器错误");
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("haha", "json 解析出错");
                }
            }
        });
    }

    private void getNowLocation(final Geocoder ge)
    {
        swipeRefreshLayout.setRefreshing(true);
        locationUtil.getLocation(new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.d("location", "获得了" + location.toString());
                newLocation = location;
                try
                {
                    StringBuilder stringBuilder = new StringBuilder(ge.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1).get(0).getSubLocality());//获取新都区
                    stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);//裁剪为新都
                    if (cityName!=null&&cityName.equals(stringBuilder.toString()))
                    {
                        return;
                    }
                    Log.d("haha", "不一样" + cityName + " and " + stringBuilder.toString());
                    cityName = stringBuilder.toString();
                    titleTxt.setText(cityName);
                    getNewWeatherInfo();
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


    /**
     * 将jsonbean转换为今日信息所需要的MainBean
     * @param jsonBean
     * @return
     */
    private MainBean converToMainBean(JsonBean jsonBean)
    {
        MainBean bean = new MainBean.Builder().windSpeed(jsonBean.realTime.windInfo.power)
                .time(jsonBean.realTime.time).date(jsonBean.realTime.week)
                .humidity(jsonBean.realTime.weatherInfo.humidity).temp(jsonBean.realTime.weatherInfo.temperature)
                .weatherInfo(jsonBean.realTime.weatherInfo.info).build();
        if (jsonBean.PM25 == null)
        {
            bean.setPmValue("..");
        } else
        {
            bean.setPmValue(jsonBean.PM25.info.pm25);
        }
        bean.setPic(BitmapFactory.decodeResource(getResources(), R.mipmap.sunny_logo));
        if (bean.getWeatherInfo().contains("多云") || bean.getWeatherInfo().contains("阴天"))
        {
            bean.setPic(BitmapFactory.decodeResource(getResources(), R.mipmap.cloudy_logo));
        } else if (bean.getWeatherInfo().contains("雨"))
        {
            bean.setPic(BitmapFactory.decodeResource(getResources(), R.mipmap.rainy_logo));
        } else if (bean.getWeatherInfo().contains("雪"))
        {
            bean.setPic(BitmapFactory.decodeResource(getResources(), R.mipmap.snowy_logo));
        }
        return bean;
    }

    /**
     * 将jsonbean转换为RecyclerView需要的List
     * @param jsonBean
     * @return
     */
    private List<PredictBean> convertToPreBeanList(JsonBean jsonBean)
    {
        List<PredictBean> list = new ArrayList<>();

        for (JsonBean.Item item : jsonBean.weather)
        {
            list.add(new PredictBean.Builder().date(item.date).
                    weekDay("星期"+item.week).describe(item.weatherInfo.day.get(1)).
                    Temp(item.weatherInfo.night.get(2)+"℃~"+item.weatherInfo.day.get(2)+"℃").build());
        }
        return list;
    }



    /**
     * 刷新今日天气信息
     * @param bean
     */
    private void reFreshTopCard(MainBean bean)
    {
        StringBuilder weekDay = new StringBuilder("星期");
        switch (bean.getDate())
        {
            case "1":
                weekDay.append("一");
                break;
            case "2":
                weekDay.append("二");
                break;
            case "3":
                weekDay.append("三");
                break;
            case "4":
                weekDay.append("四");
                break;
            case "5":
                weekDay.append("五");
                break;
            case "6":
                weekDay.append("六");
                break;
            default:
                weekDay.append("天");
                break;
        }
        todayWeekDay.setText(weekDay);
        todayLogo.setImageBitmap(bean.getPic());
        todayTemp.setText(bean.getTemp() + "℃");
        todayUpdateTime.setText("最近更新" + bean.getTime());
        todayPM25.setText(bean.getPmValue());
        todayHumidity.setText(bean.getHumidity());
        todayWindSpeed.setText(bean.getWindSpeed());
    }

    /**
     * 刷新七天天气预报
     * @param preBeanList
     */
    private void reFreshPreCard(final List<PredictBean> preBeanList)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    for (int i=1;i<preBeanList.size();i++)
                    {
                        Message message = Message.obtain();
                        message.what=ADD_PRE_BEAN;
                        message.arg1= i;
                        message.obj = preBeanList;
                        handler.sendMessage(message);
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
