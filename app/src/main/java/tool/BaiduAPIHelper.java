package tool;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/25.
 */

public class BaiduAPIHelper
{
    private String url;
    private String lng;
    private String lat;
    public BaiduAPIHelper()
    {

    }



    public List<String> getCityName(String lat,String lng)
    {
        this.lat = lat;
        this.lng = lng;
        final int[] judge = {-1};
        url="http://api.map.baidu.com/geocoder/v2/?location="+lat+","+lng+"&output=json&pois=1&ak=Q0C3cY1veuL9mzPF9wQAKuH0FF7n4Y5Q";
        final List<String> cities = new ArrayList<>();
        OkHttpClient mClient = new OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS).
                writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).build();
        final Request request = new Request.Builder().url(url).build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.d("haha", "百度地图连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String jsonString = response.body().string();
                try
                {
                    Log.d("haha", jsonString);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    cities.add(jsonObject.getJSONObject("result").getJSONObject("addressComponent").getString("city"));
                    cities.add(jsonObject.getJSONObject("result").getJSONObject("addressComponent").getString("district"));
                    Log.d("haha", cities.toString());

                    judge[0]=1;

                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("haha", "json 解析出错");
                }
            }
        });

        while (judge[0]==-1)
        {

        }
        return cities;
    }


}
