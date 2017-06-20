package tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.gaoyunfan.cardweather.R;

import Bean.JsonBean;
import Bean.MainBean;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ScreenUtil
{
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    public static MainBean converToMainBean(Context context, JsonBean jsonBean)
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
    //判断描述和图片
    public static void judgeWeatherInfo(Context context, RemoteViews views, MainBean bean, int desId, int viewId)
    {
        if (bean.getWeatherInfo().contains("多云") || bean.getWeatherInfo().contains("阴"))
        {
            if(desId != 0)
            {
                views.setImageViewBitmap(desId, buildUpdate("Cloudy", context, ScreenUtil.dp2px(context, 50f), ScreenUtil.dp2px(context, 55f),
                        ScreenUtil.dp2px(context, 200f), ScreenUtil.dp2px(context, 100f), 35f));
            }
            views.setImageViewBitmap(viewId,bean.getPic());
            return;
        } else if (bean.getWeatherInfo().contains("雨"))
        {
            if(desId != 0)
            {
                views.setImageViewBitmap(desId, buildUpdate("Rainy", context,
                        ScreenUtil.dp2px(context, 40f),ScreenUtil. dp2px(context, 55f),
                        ScreenUtil.dp2px(context, 200f), ScreenUtil.dp2px(context, 100f), 35f));
            }
            views.setImageViewBitmap(viewId,bean.getPic());
            return;
        } else if (bean.getWeatherInfo().contains("雪"))
        {
            if(desId != 0)
            {
                views.setImageViewBitmap(desId, buildUpdate("Snowy", context, ScreenUtil.dp2px(context, 40f),
                        ScreenUtil.dp2px(context, 55f), ScreenUtil.dp2px(context, 200f),ScreenUtil. dp2px(context, 100f), 35f));
            }
            views.setImageViewBitmap(viewId,bean.getPic());
            return;
        } else
        {
            if(desId != 0)
            {
                views.setImageViewBitmap(desId, buildUpdate("Sunny", context, ScreenUtil.dp2px(context, 40f),
                        ScreenUtil.dp2px(context, 55f),ScreenUtil. dp2px(context, 200f), ScreenUtil.dp2px(context, 100f), 35f));
            }
            views.setImageViewBitmap(viewId,bean.getPic());
            return;
        }
    }
    public static Bitmap buildUpdate(String temp, Context context, int x, int y, int width, int height, float fontSize){
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
        paint.setTextSize(ScreenUtil.sp2px(context,fontSize));
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(temp, x, y, paint);
        return myBitmap;
    }
}
