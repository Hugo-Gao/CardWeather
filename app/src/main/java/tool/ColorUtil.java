package tool;

import android.content.Context;

import com.gaoyunfan.cardweather.R;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ColorUtil
{
    public static int checkToColor(Context context, int temp)
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
}
