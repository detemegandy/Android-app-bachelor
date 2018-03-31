package com.example.itsme.devicebatteryexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

/**
 * Receives the intents from
 *
 * <intent-filter>
 * <action android:name="android.intent.action.BATTERY_LOW"></action>
 * <action android:name="android.intent.action.BATTERY_OKAY"></action>
</intent-filter> *
 */

class BatteryLevelReceiver//constructor gets reference to creator
(private val activity: MainActivity) : BroadcastReceiver() {

    override//executed upon receiving one of the intents
    fun onReceive(context: Context, intent: Intent) {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)


        // Are we charging / charged?
        val status = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // How are we charging(USB/AC)?
        val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        // factors for batterypct
        batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        //calculate batterypct
        batteryPct = batteryLevel / batteryScale.toFloat()
    }

    private companion object {

        private var isCharging: Boolean = false
        private var usbCharge: Boolean = false
        private var acCharge: Boolean = false
        private var batteryLevel: Int = 0
        private var batteryScale: Int = 0
        private var batteryPct: Float = 0.toFloat()
    }
}
