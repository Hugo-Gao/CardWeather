package tool;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 */

public class LocationUtil
{
    private LocationManager locationManager;
    private String provider;
    private Context context;
    private List<String> list;
    private final String TAG = "location";
    public LocationUtil(Context context)
    {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        list = locationManager.getProviders(true);
        if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.GPS_PROVIDER;

        } else {
            Toast.makeText(context, "请检查网络或GPS是否打开",
                    Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, provider + "可用");
    }

    public Location getLocation()
    {
        Location location=null;
        if (provider != null)
        {
             location = locationManager.getLastKnownLocation(provider);
            if (location != null)
            {
                String string = "纬度为：" + location.getLatitude() + ",经度为："
                        + location.getLongitude();
                Log.d("location", string);
            }
        }
        return location;
    }



}
