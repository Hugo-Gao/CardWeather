package tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArraySet;
import android.util.Log;

import java.util.Set;

/**
 * Created by Administrator on 2017/6/4.
 */

public class SPUtil
{
    public static void SaveInitialCity(Context context, String cityName)
    {
        SharedPreferences citySP = context.getSharedPreferences("initialCity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = citySP.edit();
        editor.clear();
        editor.apply();
        editor.putString("cityName", cityName);
        editor.commit();
    }
    public static String GetInitialCity(Context context)
    {
        SharedPreferences citySP = context.getSharedPreferences("initialCity", Context.MODE_PRIVATE);
        return citySP.getString("cityName", null);
    }

    public static void SaveStarCities(Context context, String cityName)
    {
        SharedPreferences starCitySP = context.getSharedPreferences("starCities", Context.MODE_PRIVATE);
        Set<String> citySet = starCitySP.getStringSet("starCitiesSet", new ArraySet<String>());
        citySet.add(cityName);
        SharedPreferences.Editor editor = starCitySP.edit();
        editor.clear();
        editor.apply();
        editor.putStringSet("starCitiesSet", citySet);
        editor.apply();
    }

    public static Set<String> GetStarCities(Context context)
    {
        SharedPreferences starCitySP = context.getSharedPreferences("starCities", Context.MODE_PRIVATE);
        Set<String> set = starCitySP.getStringSet("starCitiesSet", new ArraySet<String>());
        Log.d("haha", "得到了" + set.toString());
        return set;
    }

    public static boolean DeleteStarCity(Context context,String cityName)
    {
        SharedPreferences starCitySP = context.getSharedPreferences("starCities", Context.MODE_PRIVATE);
        Set<String> citySet = starCitySP.getStringSet("starCitiesSet", new ArraySet<String>());
        boolean isSuccess =citySet.remove(cityName);
        SharedPreferences.Editor editor = starCitySP.edit();
        editor.clear();
        editor.apply();
        editor.putStringSet("starCitiesSet", citySet);
        editor.apply();
        return isSuccess;
    }

}
