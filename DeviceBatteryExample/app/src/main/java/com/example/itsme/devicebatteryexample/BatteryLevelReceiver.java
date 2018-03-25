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

    @Override //executed upon receiving one of the intents
    public void onReceive(Context context, Intent intent) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);


        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // factors for batterypct
        int batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        //calculate batterypct
        float batteryPct = batteryLevel / (float)batteryScale;
    }
}
