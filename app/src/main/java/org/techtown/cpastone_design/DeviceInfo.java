package org.techtown.cpastone_design;

import android.content.Context;
import android.provider.Settings;

public class DeviceInfo {
    public String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
