package ir.radical_app.radical.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PermissionUtil {
    private SharedPreferences sp;

    public PermissionUtil(Activity activity){

        sp = activity.getSharedPreferences("Permission",Context.MODE_PRIVATE);
    }

    public void update(String Permission){
        switch (Permission){
            case "camera":
                sp.edit().putBoolean("camera",true).apply();
                break;
            case "audio":
                sp.edit().putBoolean("audio",true).apply();
                break;
            case "write":
                sp.edit().putBoolean("write",true).apply();
                break;

        }
    }
    public boolean check(String Permission){
        boolean isRequested=false;
        switch (Permission){
            case "camera":
                isRequested = sp.getBoolean("camera",false);
                break;
            case "audio":
                isRequested = sp.getBoolean("audio",false);
                break;
            case "write":
                isRequested = sp.getBoolean("write",false);
                break;

        }
        return isRequested;
    }

}
