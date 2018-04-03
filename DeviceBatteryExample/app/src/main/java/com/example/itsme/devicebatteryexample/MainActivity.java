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

    //variables received from intents
    private boolean isCharging;
    private boolean acCharge;
    private boolean usbCharge;
    private boolean batteryPct;

    String updateText
            = "Not updated";
    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         textView = findViewById(R.id.textView);

        registerReceiver(batteryLevel, new IntentFilter(CONNECTION_UPDATE_ACTION));
        registerReceiver(powerConnection, new IntentFilter(BATTERY_LEVEL_ACTION));
    }


    BroadcastReceiver batteryLevel = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = getIntent().getExtras();

            //needed if either
            isCharging = bundle.getBoolean("isCharging");
        }
    };


    BroadcastReceiver powerConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = getIntent().getExtras();
            isCharging = bundle.getBoolean("isCharge");
            acCharge = bundle.getBoolean("acCharge");
            usbCharge = bundle.getBoolean("usbCharge");

            updateText = "Connection changed: " + isCharging +"/n"
                    +"acCharge = " + acCharge + "/n"
                    +"usbCharge = " + usbCharge;
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
}
