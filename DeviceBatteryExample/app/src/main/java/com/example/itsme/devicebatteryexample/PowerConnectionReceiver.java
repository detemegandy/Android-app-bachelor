package com.example.itsme.devicebatteryexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
    receives the intents from

    <intent-filter>
        <action android:name="android.intent.action.ACTION_POWER_CONNECTED"/>
        <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED"/>
    </intent-filter>
 **/

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static boolean isCharging = false;
    private static boolean usbCharge = false;
    private static boolean acCharge = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        //send intent to Main Broadcastreceiver
        Intent intentToMain = new Intent(MainActivity.CONNECTION_UPDATE_ACTION)
                .putExtra("isCharging", isCharging)
                .putExtra("usbCharge", usbCharge)
                .putExtra("acCharge", acCharge);
        context.sendBroadcast(intentToMain);
    }

}