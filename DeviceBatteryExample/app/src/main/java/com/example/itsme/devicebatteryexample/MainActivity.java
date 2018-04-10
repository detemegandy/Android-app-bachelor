package com.example.itsme.devicebatteryexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //actions for implicit intents to the app
    static final String BATTERY_LEVEL_ACTION = "com.example.itsme.devicebatteryexample.batterylevel";
    public static final String CONNECTION_UPDATE_ACTION = "com.example.itsme.devicebatteryexample.action.batterylevel";
/*
    //variables received from intents
    private boolean isCharging;
    private boolean acCharge;
    private boolean usbCharge;
    private boolean batteryPct;*/


    //vars for batterylevelreceiver

    private int batteryLevel = 0;
    private int batteryScale = 0;
    private float batteryPct = 0;

    //vars for powerconnection receiver
    private static boolean isCharging = false;
    private static boolean usbCharge = false;
    private static boolean acCharge = false;

    String updateText
            = "Not updated";
    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        registerReceiver(batteryLevelReceiver, new IntentFilter(CONNECTION_UPDATE_ACTION));
        registerReceiver(powerConnection, new IntentFilter(BATTERY_LEVEL_ACTION));
    }


    BroadcastReceiver powerConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = getIntent().getExtras();
            isCharging = bundle.getBoolean("isCharge");
            acCharge = bundle.getBoolean("acCharge");
            usbCharge = bundle.getBoolean("usbCharge");

            updateText = "Connection changed: " + isCharging + "/n"
                    + "acCharge = " + acCharge + "/n"
                    + "usbCharge = " + usbCharge;
            Log.w("DeviceBatteryExample", updateText);

            //make a snackbar to show the update tekst
            Snackbar.make(findViewById(R.id.myCoordinatorLayout), updateText,
                    Snackbar.LENGTH_LONG)
                    .show();

        }
    };

    public void updateTextView(View view) {
        textView.setText(updateText);
    }


    /**
     * receives the intents from
     * <p>
     * <intent-filter>
     * <action android:name="android.intent.action.ACTION_POWER_CONNECTED"/>
     * <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED"/>
     * </intent-filter>
     **/

    public class PowerConnectionReceiver extends BroadcastReceiver {


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


    /**
     * Receives the intents from
     * <p>
     * <intent-filter>
     * <action android:name="android.intent.action.BATTERY_LOW"/>
     * <action android:name="android.intent.action.BATTERY_OKAY"/>
     * </intent-filter>
     */

    public class BatteryLevelReceiver extends BroadcastReceiver {

        @Override //executed upon receiving one of the intents
        public void onReceive(Context context, Intent batteryStatus) {

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
            batteryPct = batteryLevel / (float) batteryScale;

            Intent intentToMain = new Intent(MainActivity.BATTERY_LEVEL_ACTION);
            intentToMain.putExtra("isCharging", isCharging);
            intentToMain.putExtra("usbCharging", usbCharge);
            intentToMain.putExtra("acCharging", acCharge);
            intentToMain.putExtra("batteryPct", batteryPct);
            context.sendBroadcast(intentToMain);
        }
    }
}
