package layout;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tool.SPUtil;

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
                        MainBean bean = converToMainBean(context, jsonBean);
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

        views.setImageViewBitmap(R.id.widget_temp, buildUpdate(bean.getTemp() + "°", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 120f), 65f));
        judgeWeatherInfo(context,views, bean);
        views.setTextViewText(R.id.widget_temp_range, jsonBean.weather.get(0).weatherInfo.night.get(2) + "°~" + jsonBean.weather.get(0).weatherInfo.day.get(2) + "°");
        int temp = Integer.parseInt(bean.getTemp());
        views.setInt(R.id.main_card2, "setBackgroundColor", checkToColor(context, temp ));
        views.setTextViewText(R.id.widget_update_time, jsonBean.realTime.time + " 更新");
    }

    //判断描述和图片
    private void judgeWeatherInfo(Context context,RemoteViews views, MainBean bean)
    {
        if (bean.getWeatherInfo().contains("多云") || bean.getWeatherInfo().contains("阴"))
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Cloudy", context, dp2px(context, 50f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic,bean.getPic());
            return;
        } else if (bean.getWeatherInfo().contains("雨"))
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Rainy", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic,bean.getPic());
            return;
        } else if (bean.getWeatherInfo().contains("雪"))
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Snowy", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic,bean.getPic());

            return;
        } else
        {
            views.setImageViewBitmap(R.id.widget_des, buildUpdate("Sunny", context, dp2px(context, 40f), dp2px(context, 55f), dp2px(context, 200f), dp2px(context, 100f), 35f));
            views.setImageViewBitmap(R.id.widget_pic,bean.getPic());
            return;
        }
    }

    private MainBean converToMainBean(Context context,JsonBean jsonBean)
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
    private int checkToColor(Context context,int temp)
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

    static Bitmap buildUpdate(String temp, Context context,int x,int y,int width,int height,float fontSize){
        Bitmap myBitmap = Bitmap.createBitmap(width ,height, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface tf= Typeface.createFromAsset(context.getAssets(),"fonts/Pacifico.ttf");
        paint.setAntiAlias(true);
        paint.setAlpha(110);// 取值范围为 0~255，值越小越透明
        paint.setSubpixelText(true);
        paint.setTypeface(tf);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.WHITE);
        paint.setTextSize(sp2px(context,fontSize));
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(temp, x, y, paint);
        return myBitmap;
    }
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }
    /**
     * 当 Widget 被删除时调用该方法。
     *
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 当 Widget 第一次被添加时调用，例如用户添加了两个你的 Widget，那么只有在添加第一个 Widget 时该方法会被调用。
     * 所以该方法比较适合执行你所有 Widgets 只需进行一次的操作
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 与 onEnabled 恰好相反，当你的最后一个 Widget 被删除时调用该方法，所以这里用来清理之前在 onEnabled() 中进行的操作。
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 当 Widget 第一次被添加或者大小发生变化时调用该方法，可以在此控制 Widget 元素的显示和隐藏。
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }


}

