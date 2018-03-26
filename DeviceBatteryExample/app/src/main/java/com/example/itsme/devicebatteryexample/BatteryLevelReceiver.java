package com.example.itsme.devicebatteryexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Receives the intents from
 *
 <intent-filter>
 <action android:name="android.intent.action.BATTERY_LOW"/>
 <action android:name="android.intent.action.BATTERY_OKAY"/>
 </intent-filter>
 */

public class BatteryLevelReceiver extends BroadcastReceiver {

    private boolean isCharging;
    private boolean usbCharge;
    private boolean acCharge;
    private int batteryLevel;
    private int batteryScale;
    private float batteryPct;

    @Override //executed upon receiving one of the intents
    public void onReceive(Context context, Intent intent) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);


        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // factors for batterypct
        batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        //calculate batterypct
        batteryPct = batteryLevel / (float)batteryScale;
    }
}
