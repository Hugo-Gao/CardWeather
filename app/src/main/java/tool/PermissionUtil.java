package tool;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by Administrator on 2017/5/16.
 */

public class PermissionUtil
{


    private static final String[] perms = {"android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"};
    private static final int permsRequestCode=200;


    public static void getPermission(Context context)
    {
        if(!hasPermission(perms,context))
        {
            requestPermissions((Activity) context,perms,permsRequestCode);
        }
    }

    private static boolean hasPermission(String[] permissions, Context context)
    {
        if(OverLollipop())
        {
            for (String permission : permissions)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED)
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }




    /**
     * 检查是否需要大于Android 6.0
     * @return
     */
    private static boolean OverLollipop()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }
}
