package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tool.ColorUtil;
import tool.SPUtil;
import tool.ScreenUtil;

import static tool.ScreenUtil.buildUpdate;
import static tool.ScreenUtil.judgeWeatherInfo;

/**
 * Implementation of App Widget functionality.
 */
public class MainCardWidget extends AppWidgetProvider
{
    private final String AppKey = "5525d200e35c443eb70948bc960141b3";
    private final String apiUri = "http://api.avatardata.cn/Weather/Query";
    public MainCardWidget()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    /**
     * 根据 updatePeriodMillis 定义的定期刷新操作会调用该函数，此外当用户添加 Widget 时也会调用该函数，可以在这里进行必要的初始化操作。
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i("shenlong", "onUpdate");
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            Log.i("shenlong", "onUpdate appWidgetId=" + appWidgetId);
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.setClass(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_card_widget);
            views.setOnClickPendingIntent(R.id.main_card2, pendingIntent);
            //刷新控件
            String cityName= SPUtil.GetInitialCity(context);
            views.setTextViewText(R.id.widget_local_city,cityName);
            getNewWeatherInfo(context,cityName,appWidgetManager,appWidgetId,views);

        }
    }

    private int getNewWeatherInfo(final Context context, final String cityName, final AppWidgetManager appWidgetManager, final int appWidgetId, final RemoteViews views)
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
                appWidgetManager.updateAppWidget(appWidgetId, views);
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
                        Log.d("haha", "widget获取到正确json");
                        JSONObject resultobject = jsonObject.getJSONObject("result");
                        JsonBean jsonBean = gson.fromJson(String.valueOf(resultobject), JsonBean.class);
                        MainBean bean =ScreenUtil.converToMainBean(context, jsonBean);
                        refreshViews(context,bean,jsonBean,views);
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    } else
                    {
                        Log.d("haha", "widget网络畅通，服务器出现问题");
                    }
                    values[0]=1;
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    values[0]=1;
                    Log.d("haha", "json 解析出错");
                }
            }
        });
        while(values[0]!=1)
        {
            Log.d("haha", "widget等待数据");
        }
        return values[0];
    }

    private void refreshViews(Context context,MainBean bean,JsonBean jsonBean, RemoteViews views)
    {

        views.setImageViewBitmap(R.id.widget_temp, buildUpdate(bean.getTemp() + "°", context, ScreenUtil.dp2px(context, 40f),
                ScreenUtil.dp2px(context, 55f),ScreenUtil. dp2px(context, 200f), ScreenUtil.dp2px(context, 120f), 65f));
        judgeWeatherInfo(context,views, bean,R.id.widget_des,R.id.widget_pic);
        views.setTextViewText(R.id.widget_temp_range, jsonBean.weather.get(0).weatherInfo.night.get(2) + "°~" + jsonBean.weather.get(0).weatherInfo.day.get(2) + "°");
        int temp = Integer.parseInt(bean.getTemp());
        views.setInt(R.id.main_card2, "setBackgroundColor", ColorUtil.checkToColor(context, temp ));
        views.setTextViewText(R.id.widget_update_time, jsonBean.realTime.time + " 更新");
    }










}

