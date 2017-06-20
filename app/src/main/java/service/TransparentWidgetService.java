package service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
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
import layout.TransParentWidget;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tool.SPUtil;

import static tool.ScreenUtil.converToMainBean;
import static tool.ScreenUtil.judgeWeatherInfo;

/**
 * Created by Administrator on 2017/6/20.
 */

public class TransparentWidgetService extends Service
{
    private final String AppKey = "5525d200e35c443eb70948bc960141b3";
    private final String apiUri = "http://api.avatardata.cn/Weather/Query";
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d("trans", "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("trans", "onStartCommand");
        updateWidget();
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWidget()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.trans_parent_widget);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(TransparentWidgetService.this);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                intent.setClass(TransparentWidgetService.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(TransparentWidgetService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.main_card3, pendingIntent);
                //刷新控件
                String cityName = SPUtil.GetInitialCity(TransparentWidgetService.this);
                getNewWeatherInfo(TransparentWidgetService.this, cityName, appWidgetManager, views);
            }
        }).start();
    }
    private int getNewWeatherInfo(final Context context, final String cityName, final AppWidgetManager appWidgetManager, final RemoteViews views)
    {
        final int[] values = {-1};
        final Gson gson = new Gson();
        Log.d("trans", "进入getNewWeatherInfo");
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
                Log.d("trans", "widget无法连接服务器,failure");
                appWidgetManager.updateAppWidget(new ComponentName(TransparentWidgetService.this, TransParentWidget.class), views);
                values[0]=1;
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
                        Log.d("trans", "widget获取到正确json");
                        JSONObject resultobject = jsonObject.getJSONObject("result");
                        JsonBean jsonBean = gson.fromJson(String.valueOf(resultobject), JsonBean.class);
                        MainBean bean = converToMainBean(context, jsonBean);
                        refreshViews(context,bean,jsonBean,views);
                        appWidgetManager.updateAppWidget(new ComponentName(TransparentWidgetService.this, TransParentWidget.class), views);
                    } else
                    {
                        Log.d("trans", "widget网络畅通，服务器出现问题");
                    }
                    values[0]=1;
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    values[0]=1;
                    Log.d("trans", "json 解析出错");
                }
            }
        });
        while(values[0]!=1)
        {
        }
        return values[0];
    }

    private void refreshViews(Context context, MainBean bean, JsonBean jsonBean, RemoteViews views)
    {
        judgeWeatherInfo(context,views,bean,0,R.id.trans_widget_weather_pic);
        views.setTextViewText(R.id.trans_widget_temp,bean.getTemp()+"℃");
        String date=getDateString(jsonBean);
        views.setTextViewText(R.id.trans_widget_date,date);
    }

    private String getDateString(JsonBean jsonBean)
    {
        StringBuilder date = new StringBuilder(jsonBean.weather.get(0).date.substring(5)+" ");
        date.append("周").append(jsonBean.weather.get(0).week);
        return date.toString();
    }
}
