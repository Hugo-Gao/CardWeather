package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.gaoyunfan.cardweather.MainActivity;
import com.gaoyunfan.cardweather.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import Bean.JsonBean;
import Bean.MainBean;
import layout.MainCardWidget;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tool.SPUtil;

/**
 * Created by Administrator on 2017/6/13.
 */

public class WidgetUpdateService extends Service
{

    private static final int ALARM_DURATION = 31 * 60 * 1000; // service 自启间隔
    private static final int UPDATE_DURATION = 31 * 60 * 1000;     // Widget 更新间隔
    private static final int UPDATE_MESSAGE = 1000;
    private final String AppKey = "5525d200e35c443eb70948bc960141b3";
    private final String apiUri = "http://api.avatardata.cn/Weather/Query";
    private UpdateHandler updateHandler; // 更新 Widget 的 Handler

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("haha", "onStartCommand");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getBaseContext(), WidgetUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + ALARM_DURATION, pendingIntent);
        updateWidget();
        return START_STICKY;
    }


    @Override
    public void onCreate()
    {
        updateHandler = new UpdateHandler();
        Message message = updateHandler.obtainMessage();
        message.what = UPDATE_MESSAGE;
        updateHandler = new UpdateHandler();
        updateHandler.sendMessage(message);
    }


    private void updateWidget()
    {
        // 更新 Widget
        Log.d("haha", "updateWidget");
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.main_card_widget);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetUpdateService.this);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                intent.setClass(WidgetUpdateService.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(WidgetUpdateService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.main_card2, pendingIntent);
                //刷新控件
                String cityName = SPUtil.GetInitialCity(WidgetUpdateService.this);
                views.setTextViewText(R.id.widget_local_city, cityName);
                getNewWeatherInfo(WidgetUpdateService.this, cityName, appWidgetManager, views);
            }
        }).start();


        // 发送下次更新的消息
        Message message = updateHandler.obtainMessage();
        message.what = UPDATE_MESSAGE;
        updateHandler.sendMessageDelayed(message, UPDATE_DURATION);
    }


    private int getNewWeatherInfo(final Context context, final String cityName, final AppWidgetManager appWidgetManager, final RemoteViews views)
    {
        final int[] values = {-1};
        final Gson gson = new Gson();
        Log.d("haha", "进入getNewWeatherInfo");
        OkHttpClient mClient = new OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS).
                writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).build();

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
                Log.d("haha", "widget无法连接服务器,failure");
                appWidgetManager.updateAppWidget(new ComponentName(WidgetUpdateService.this, MainCardWidget.class), views);
                values[0] = 1;
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
                        Log.d("haha", "widget获取到正确json");
                        JSONObject resultobject = jsonObject.getJSONObject("result");
                        JsonBean jsonBean = gson.fromJson(String.valueOf(resultobject), JsonBean.class);
                        MainBean bean = converToMainBean(context, jsonBean);
                        refreshViews(context, bean, jsonBean, views);
                        appWidgetManager.updateAppWidget(new ComponentName(WidgetUpdateService.this, MainCardWidget.class), views);

                    } else
                    {
                        Log.d("haha", "widget网络畅通，服务器出现问题");
                    }
                    values[0] = 1;
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    values[0] = 1;
                    Log.d("haha", "json 解析出错");
                }
            }
        });
        while (values[0] != 1)
        {

        }
        return values[0];
    }

    private void refreshViews(Context context, MainBean bean, JsonBean jsonBean, RemoteViews views)
    {

        views.setImageViewBitmap(R.id.widget_temp, buildUpdate(bean.getTemp() + "°", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 120f), 65f));
        judgeWeatherInfo(context, views, bean);
        views.setTextViewText(R.id.widget_temp_range, jsonBean.weather.get(0).weatherInfo.night.get(2) + "°~" + jsonBean.weather.get(0).weatherInfo.day.get(2) + "°");
        int temp = Integer.parseInt(bean.getTemp());
        views.setInt(R.id.main_card2, "setBackgroundColor", checkToColor(context, temp));
        views.setTextViewText(R.id.widget_update_time, jsonBean.realTime.time + " 更新");
    }

    //判断描述和图片
    private void judgeWeatherInfo(Context context, RemoteViews views, MainBean bean)
    {
        if (bean.getWeatherInfo().contains("多云") || bean.getWeatherInfo().contains("阴"))
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Cloudy", context, dp2px(context, 50f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic, bean.getPic());
            return;
        } else if (bean.getWeatherInfo().contains("雨"))
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Rainy", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic, bean.getPic());
            return;
        } else if (bean.getWeatherInfo().contains("雪"))
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Snowy", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic, bean.getPic());

            return;
        } else
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Sunny", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic, bean.getPic());
            return;
        }
    }

    private MainBean converToMainBean(Context context, JsonBean jsonBean)
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
        bean.setPic(BitmapFactory.decodeResource(context.getResources(), R.mipmap.sunny_logo));

        if (bean.getWeatherInfo().contains("多云") || bean.getWeatherInfo().contains("阴"))
        {
            bean.setPic(BitmapFactory.decodeResource(context.getResources(), R.mipmap.cloudy_logo));
        } else if (bean.getWeatherInfo().contains("雨"))
        {
            bean.setPic(BitmapFactory.decodeResource(context.getResources(), R.mipmap.rainy_logo));
        } else if (bean.getWeatherInfo().contains("雪"))
        {
            bean.setPic(BitmapFactory.decodeResource(context.getResources(), R.mipmap.snowy_logo));
        }
        return bean;
    }

    private int checkToColor(Context context, int temp)
    {
        int toColor = context.getResources().getColor(R.color.fineColor);
        if (temp >= 30)
        {
            toColor = context.getResources().getColor(R.color.veryHotColor);
        } else if (temp >= 25 && temp < 30)
        {
            toColor = context.getResources().getColor(R.color.hotColor);
        } else if (temp >= 20 && temp < 25)
        {
            toColor = context.getResources().getColor(R.color.coolColor);
        } else if (temp >= 15 && temp < 20)
        {
            toColor = context.getResources().getColor(R.color.fineColor);
        } else if (temp >= 10 && temp < 15)
        {
            toColor = context.getResources().getColor(R.color.underFineColor);
        } else if (temp >= 5 && temp < 10)
        {
            toColor = context.getResources().getColor(R.color.coldColor);
        } else if (temp >= 0 && temp < 5)
        {
            toColor = context.getResources().getColor(R.color.veryColdColor);
        } else if (temp < 0)
        {
            toColor = context.getResources().getColor(R.color.frazeColor);
        }
        return toColor;
    }

    static Bitmap buildUpdate(String temp, Context context, int x, int y, int width, int height, float fontSize)
    {
        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Pacifico.ttf");
        paint.setAntiAlias(true);
        paint.setAlpha(110);// 取值范围为 0~255，值越小越透明
        paint.setSubpixelText(true);
        paint.setTypeface(tf);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.WHITE);
        paint.setTextSize(sp2px(context, fontSize));
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(temp, x, y, paint);
        return myBitmap;
    }

    public static int sp2px(Context context, float spValue)
    {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }


    protected final class UpdateHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UPDATE_MESSAGE:
                    Log.d("haha", "进入handler");
                    updateWidget();
                    break;
                default:
                    break;
            }
        }
    }

}
