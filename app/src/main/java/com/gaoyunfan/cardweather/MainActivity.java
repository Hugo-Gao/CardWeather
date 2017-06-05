package com.gaoyunfan.cardweather;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lljjcoder.citypickerview.widget.CityPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import Bean.JsonBean;
import Bean.MainBean;
import Bean.PredictBean;
import adapter.CityCollectorAdapter;
import adapter.FutureCardAdapter;
import adapter.MyCallBack;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tool.AnimationUtil;
import tool.LocationUtil;
import tool.RecyScrollListener;
import tool.SPUtil;

public class MainActivity extends Activity
{

    private LocationUtil locationUtil;
    private Location newLocation;
    private String cityName;
    private String tempCityName;
    private List<PredictBean> preBeanList;
    private FutureCardAdapter adapter;
    private final String AppKey = "5525d200e35c443eb70948bc960141b3";
    private final String apiUri = "http://api.avatardata.cn/Weather/Query";
    private Gson gson;
    private final int GET_JSON = 1;
    private final int REMOVE_ALL = 2;
    private final int ADD_PRE_BEAN = 3;
    private boolean fromLocal = true;
    private MainBean overAllBean;
    private JsonBean overAllJsonBean;
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
                        refreshLayout(jsonBean);
                    }
                    break;
                case REMOVE_ALL:
                    adapter.removeAll();
                    break;
                case ADD_PRE_BEAN:
                    int index = msg.arg1;
                    List<PredictBean> list = (List<PredictBean>) msg.obj;
                    if (list.size() == 0)
                    {
                        break;
                    }
                    adapter.addItem(list.get(index));
                    if (index == list.size() - 1 && !fromLocal)
                    {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                    if (index == 1)
                    {
                        if (!fromLocal)
                        {
                            int fromColor = ((ColorDrawable) topLayout.getBackground()).getColor();
                            int toColor = checkToColor();
                            AnimationUtil.ColorChangeAnimation(topLayout, fromColor, toColor);
                        }
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

    @BindView(R.id.main_card)
    CardView mainCard;

    @OnClick(R.id.main_card)
    public void cardClick(CardView cardView)
    {
        if (overAllBean != null && overAllJsonBean != null)
        {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("which", overAllBean.getWeatherInfo());
            intent.putExtra("jsonBean", overAllJsonBean);
            if (OverLollipop())
            {
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this,
                                mainCard, mainCard.getTransitionName());
                startActivityForResult(intent, 1, options.toBundle());
            } else
            {
                startActivity(intent);
            }
        }

    }

    @OnClick(R.id.floatbutton)
    public void fabClick(FloatingActionButton fab)
    {
        AnimationUtil.FABRotate(fab);
        showBottomDialog();


    }

    @OnClick(R.id.get_local)
    public void localClick(Button button)
    {
        AnimationUtil.ConfirmAndBigger(button);
        cityName = null;
        tempCityName = null;
        final Geocoder ge = new Geocoder(this);
        getNowLocation(ge);
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

    @BindView(R.id.top_card_layout)
    LinearLayout topLayout;

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

        preBeanList = new ArrayList<>();
        locationUtil = new LocationUtil(this);
        adapter = new FutureCardAdapter(this, preBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DefaultItemAnimator animator = new DefaultItemAnimator();
        recyclerView.setItemAnimator(animator);
        recyclerView.getItemAnimator().setAddDuration(500);
        recyclerView.setAdapter(adapter);
        final Geocoder ge = new Geocoder(this);
        gson = new Gson();
        if (SPUtil.GetInitialCity(this) != null)
        {
            cityName = SPUtil.GetInitialCity(this);
            Log.d("haha", "获取到默认城市" + cityName);
            swipeRefreshLayout.setRefreshing(true);
            getNewWeatherInfo();
        } else
        {
            Log.d("haha", "没有获取到默认城市");
            getNowLocation(ge);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if (cityName == null)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    getNowLocation(ge);
                    return;
                }
                getNewWeatherInfo();
            }
        });

        recyclerView.addOnScrollListener(new RecyScrollListener(mainCard));

    }


    private void getNewWeatherInfo()
    {
        Log.d("haha", "进入getNewWeatherInfo");
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
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toasty.error(MainActivity.this, "服务器未连接,请下拉刷新重试", Toast.LENGTH_SHORT, true).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
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
                        //序列化保存到本地
                        saveJsonBean(jsonBean);
                        Message message = Message.obtain();
                        message.what = GET_JSON;
                        fromLocal = false;
                        message.obj = jsonBean;
                        handler.sendMessage(message);
                    } else
                    {
                        Log.d("haha", "重新发送网络请求");
                        cityName = tempCityName;
                        getNewWeatherInfo();
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("haha", "json 解析出错");
                }
            }
        });
    }

    /**
     * 将jsonBean序列化保存到本地
     *
     * @param jsonBean
     */
    private void saveJsonBean(JsonBean jsonBean)
    {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try
        {
            File file = new File(Environment.getExternalStorageDirectory().toString()
                    + "/" + "weatherbean.dat");
            if (!file.exists())
            {
                file.createNewFile();
            }
            fos = new FileOutputStream(file.toString());
            oos = new ObjectOutputStream(fos);
            oos.writeObject(jsonBean);
            Log.d("haha", "序列化保存成功");
        } catch (Exception e)
        {
            Log.i("haha", e.toString());
        } finally
        {
            try
            {
                if (oos != null)
                    oos.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * 从本地读取序列化的jsonBean
     *
     * @return
     */
    private JsonBean getJsonFormLocal()
    {
        JsonBean jsonBean = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        File file = new File(Environment.getExternalStorageDirectory().toString()
                + "/" + "weatherbean.dat");
        if (file.exists())
        {
            try
            {
                fis = new FileInputStream(file.toString());
                ois = new ObjectInputStream(fis);
                jsonBean = (JsonBean) ois.readObject();
            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                try
                {
                    if (ois != null)
                    {
                        ois.close();
                    }
                    if (fis != null)
                    {
                        fis.close();
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return jsonBean;
    }

    private void getNowLocation(final Geocoder ge)
    {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                JsonBean jsonBean = getJsonFormLocal();
                if (jsonBean != null)
                {
                    Message message = Message.obtain();
                    message.obj = jsonBean;
                    message.what = GET_JSON;
                    fromLocal = true;
                    handler.sendMessage(message);
                }
            }
        }).start();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                newLocation = locationUtil.getLocation();
                try
                {
                    tempCityName = ge.getFromLocation(newLocation.getLatitude(), newLocation.getLongitude(), 1).get(0).getLocality();
                    StringBuilder stringBuilder = new StringBuilder(ge.getFromLocation(newLocation.getLatitude(),
                            newLocation.getLongitude(), 1).get(0).getSubLocality());//获取新都区
                    stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);//裁剪为新都
                    Log.d("location", "获得了" + stringBuilder.toString());
                    if (cityName != null && (cityName.equals(stringBuilder.toString()) || cityName.equals(tempCityName)))
                    {
                        return;
                    }
                    Log.d("location", "不一样" + cityName + " and " + stringBuilder.toString());
                    cityName = stringBuilder.toString();
                    getNewWeatherInfo();
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("haha", "获取地理位置出错");
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toasty.error(MainActivity.this, "未获取到你的位置,请检查网络连接", Toast.LENGTH_SHORT, true).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }

            }
        }).start();


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
     *
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
     *
     * @param jsonBean
     * @return
     */
    private List<PredictBean> convertToPreBeanList(JsonBean jsonBean)
    {
        List<PredictBean> list = new ArrayList<>();

        for (JsonBean.Item item : jsonBean.weather)
        {
            list.add(new PredictBean.Builder().date(item.date).
                    weekDay("星期" + item.week).describe(item.weatherInfo.day.get(1)).
                    Temp(item.weatherInfo.night.get(2) + "℃~" + item.weatherInfo.day.get(2) + "℃").build());
        }
        return list;
    }


    /**
     * 刷新今日天气信息
     *
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
     *
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
                    for (int i = 1; i < preBeanList.size(); i++)
                    {
                        Message message = Message.obtain();
                        message.what = ADD_PRE_BEAN;
                        message.arg1 = i;
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

    private void refreshLayout(JsonBean jsonBean)
    {
        overAllJsonBean = jsonBean;
        MainBean bean = converToMainBean(jsonBean);
        overAllBean = bean;
        preBeanList = convertToPreBeanList(jsonBean);
        reFreshTopCard(bean);
        reFreshPreCard(preBeanList);
        titleTxt.setText(cityName);
    }

    private int checkToColor()
    {
        StringBuilder text = new StringBuilder(todayTemp.getText());
        text = text.deleteCharAt(text.length() - 1);
        int temp = Integer.parseInt(text.toString());
        int toColor = this.getResources().getColor(R.color.fineColor);
        if (temp >= 30)
        {
            toColor = this.getResources().getColor(R.color.veryHotColor);
        } else if (temp >= 25 && temp < 30)
        {
            toColor = this.getResources().getColor(R.color.hotColor);
        } else if (temp >= 20 && temp < 25)
        {
            toColor = this.getResources().getColor(R.color.coolColor);
        } else if (temp >= 15 && temp < 20)
        {
            toColor = this.getResources().getColor(R.color.fineColor);
        } else if (temp >= 10 && temp < 15)
        {
            toColor = this.getResources().getColor(R.color.underFineColor);
        } else if (temp >= 5 && temp < 10)
        {
            toColor = this.getResources().getColor(R.color.coldColor);
        } else if (temp >= 0 && temp < 5)
        {
            toColor = this.getResources().getColor(R.color.veryColdColor);
        } else if (temp < 0)
        {
            toColor = this.getResources().getColor(R.color.frazeColor);
        }
        return toColor;
    }

    private void showBottomDialog()
    {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.bottom_dialog_layout, null);
        bottomDialog.setContentView(contentView);

        TextView chooseCityTv = (TextView) bottomDialog.findViewById(R.id.choose_city);
        final Drawable chooseCity = getResources().getDrawable(R.mipmap.choosecity);
        chooseCity.setBounds(0, 0, dp2px(this,50f), dp2px(this,50f));
        chooseCityTv.setCompoundDrawables(chooseCity, null, null, null);//只放左边

        TextView collectCityTv = (TextView) bottomDialog.findViewById(R.id.collect_this_city);
        final Drawable collectCity = getResources().getDrawable(R.mipmap.collect);
        collectCity.setBounds(0, 0, dp2px(this,50f), dp2px(this,50f));
        collectCityTv.setCompoundDrawables(collectCity, null, null, null);

        TextView cityCollectionsTv = (TextView) bottomDialog.findViewById(R.id.city_collections);
        final Drawable cityCollections = getResources().getDrawable(R.mipmap.colletions);
        cityCollections.setBounds(0, 0, dp2px(this,50f), dp2px(this,50f));
        cityCollectionsTv.setCompoundDrawables(cityCollections, null, null, null);

        final TextView initialCityTv = (TextView) bottomDialog.findViewById(R.id.city_initial);
        initialCityTv.setText(" 设置" + cityName + "为默认城市 ");
        Drawable initialPic = getResources().getDrawable(R.mipmap.initial);
        initialPic.setBounds(0, 0, dp2px(this,50f), dp2px(this,50f));
        initialCityTv.setCompoundDrawables(initialPic, null, null, null);


        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - dp2px(this, 16f);
        params.bottomMargin = dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.show();
        bottomDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                bottomDialog.dismiss();
            }
        });
        bottomDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                AnimationUtil.FABRotate(fab);
            }
        });
        chooseCityTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bottomDialog.dismiss();
                CityPicker cityPicker = new CityPicker.Builder(MainActivity.this)
                        .textSize(20)
                        .title("地址选择")
                        .backgroundPop(0xa0000000)
                        .titleBackgroundColor("#FFFFFF")
                        .titleTextColor("#000000")
                        .confirTextColor("#000000")
                        .cancelTextColor("#000000")
                        .province("四川省")
                        .city("成都市")
                        .district("新都区")
                        .textColor(Color.parseColor("#000000"))
                        .provinceCyclic(false)
                        .cityCyclic(false)
                        .districtCyclic(false)
                        .visibleItemsCount(7)
                        .itemPadding(10)
                        .onlyShowProvinceAndCity(false)
                        .build();
                cityPicker.show();


                //监听方法，获取选择结果
                cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener()
                {
                    @Override
                    public void onSelected(String... citySelected)
                    {
                        //省份
                        String province = citySelected[0];
                        //城市
                        String city = citySelected[1];
                        //区县（如果设定了两级联动，那么该项返回空）
                        String district = citySelected[2];
                        Log.d("haha", "选择了" + province + " " + city + " " + district);
                        if ((district.charAt(district.length() - 1) == '区' || district.charAt(district.length() - 1) == '县') && district.length() > 2)
                        {
                            StringBuilder cityNameBuilder = new StringBuilder(district);
                            cityName = cityNameBuilder.substring(0, cityNameBuilder.length() - 1);
                        } else
                        {
                            cityName = district;
                        }
                        tempCityName = city;
                        getNewWeatherInfo();
                        swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onCancel()
                    {
                    }
                });
            }
        });

        initialCityTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bottomDialog.dismiss();
                String target = (cityName != null ? cityName : tempCityName);
                if (target != null)
                {
                    SPUtil.SaveInitialCity(MainActivity.this, target);
                    Toasty.success(MainActivity.this, "设置" + target + "为默认城市成功", Toast.LENGTH_SHORT, true).show();
                } else
                {
                    Toasty.error(MainActivity.this, "对不起，你还没有添加当前城市", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        collectCityTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bottomDialog.dismiss();
                String target = (cityName != null ? cityName : tempCityName);
                if (target != null)
                {
                    SPUtil.SaveStarCities(MainActivity.this, target);
                    Toasty.success(MainActivity.this, "收藏" + target + "成功", Toast.LENGTH_SHORT, true).show();
                } else
                {
                    Toasty.error(MainActivity.this, "对不起，你还没有添加当前城市", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        cityCollectionsTv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bottomDialog.dismiss();
                Set<String> citySet = SPUtil.GetStarCities(MainActivity.this);
                final Dialog collectionsDialog = new Dialog(MainActivity.this, R.style.BottomDialog);
                View contentView2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.city_collect_layout, null);
                collectionsDialog.setContentView(contentView2);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView2.getLayoutParams();
                params.width = getResources().getDisplayMetrics().widthPixels - dp2px(MainActivity.this, 16f);
                params.height = dp2px(MainActivity.this, 300f);
                params.bottomMargin = dp2px(MainActivity.this, 8f);
                contentView2.setLayoutParams(params);
                collectionsDialog.getWindow().setGravity(Gravity.BOTTOM);
                collectionsDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
                collectionsDialog.setCanceledOnTouchOutside(true);
                collectionsDialog.show();
                collectionsDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        collectionsDialog.dismiss();
                    }
                });
                collectionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        AnimationUtil.FABRotateToNormal(fab);
                    }
                });
                RecyclerView recyclerView = (RecyclerView) collectionsDialog.findViewById(R.id.city_collect_recycler);
                TextView titleTv = (TextView) collectionsDialog.findViewById(R.id.collection_title);
                List<String> list = new ArrayList<>(citySet);
                if (list.size() == 0)
                {
                    titleTv.setText("您还没有收藏城市,请先选择城市收藏");
                }
                final CityCollectorAdapter adapter = new CityCollectorAdapter(MainActivity.this, list);
                ItemTouchHelper helper = new ItemTouchHelper(new MyCallBack(adapter));
                helper.attachToRecyclerView(recyclerView);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new CityCollectorAdapter.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, String name)
                    {
                        cityName = name;
                        tempCityName = null;
                        getNewWeatherInfo();
                        swipeRefreshLayout.setRefreshing(true);
                        collectionsDialog.dismiss();
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                collectionsDialog.show();


            }
        });
    }

    private boolean OverLollipop()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

}
