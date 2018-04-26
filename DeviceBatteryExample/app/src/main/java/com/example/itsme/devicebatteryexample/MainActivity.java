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
import android.widget.Toast;
import com.android.internal.util.XmlUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    //actions for explicit intents to the app
    final String BATTERY_LEVEL_ACTION = "com.example.itsme.devicebatteryexample.batterylevel";
    final String BATTERY_CONNECTION_UPDATE_ACTION = "com.example.itsme.devicebatteryexample.action.batterylevel";


    //actions used by inner classes
    //for connectiontype
    final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
    final String ACTION_POWER_DISCONNECTED ="android.intent.action.ACTION_POWER_DISCONNECTED";

    //for batterystatus (15% battery threshold intents)
    final String BATTERY_LOW = Intent.ACTION_BATTERY_LOW;
    final String BATTERY_OKAY = Intent.ACTION_BATTERY_OKAY;
    final String BATTERY_CHANGED = Intent.ACTION_BATTERY_CHANGED;


    //tag for log in this class
    final String TAG = this.getClass().getSimpleName();


    //vars for batterylevelreceiver

    private static int batteryLevel = -1;
    private static int batteryScale = -1;
    private static float batteryPct = -1;

    //vars for powerconnection receiver
    private static boolean isCharging = false;
    private static boolean usbCharge = false;
    private static boolean acCharge = false;

    Calendar upDateTimeCal = Calendar.getInstance();
    String updateText  = "Not updated";
    Button button;
    TextView statusText;
    private long batteryRemainingNanoWatt;
    private BatteryManager batteryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText = findViewById(R.id.textView);
        batteryManager = (BatteryManager)getSystemService(Context.BATTERY_SERVICE);
        initializeAndRegisterReceivers();
        Log.d(TAG, "onCreate finished");
    }

    private void initializeAndRegisterReceivers() {
        BatteryLevelReceiver batteryLevelReceiver = new BatteryLevelReceiver();
        PowerConnectionReceiver powerConnectionReceiver = new PowerConnectionReceiver();
        registerReceiver(batteryLevelReceiver, new IntentFilter(BATTERY_CHANGED));
        registerReceiver(powerConnectionReceiver, new IntentFilter(ACTION_POWER_CONNECTED));
        registerReceiver(powerConnectionReceiver, new IntentFilter(ACTION_POWER_DISCONNECTED));
    }

    public void btnClicked(View view) {
        updateText("btnClicked");
    }


    /**
     * receives the intents
     *
     * action android:name=android.intent.action.POWER_CONNECTED
     * action android:name=android.intent.action.POWER_DISCONNECTED
     *
     **/
    public class PowerConnectionReceiver extends BroadcastReceiver {

        final String TAG = this.getClass().getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"OnReceive started");

            //get fresh values from sticky intent
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null,intentFilter);


            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            updateText(TAG);

            //make a snackbar to show the update tekst
            Snackbar.make(findViewById(R.id.myCoordinatorLayout), updateText,
                    Snackbar.LENGTH_LONG)
                    .show();

            Log.d(TAG,"OnReceive ended");
        }

        /*@Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            //send intent to Main Broadcastreceiver
            Intent intentToMain = new Intent(MainActivity.BATTERY_CONNECTION_UPDATE_ACTION)
                    .putExtra("isCharging", isCharging)
                    .putExtra("usbCharge", usbCharge)
                    .putExtra("acCharge", acCharge);
            context.sendBroadcast(intentToMain);
        }*/


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

        final String TAG = this.getClass().getSimpleName();
        @Override //executed upon receiving one of the intents
        public void onReceive(Context context, Intent batteryStatus) {

            Log.d(TAG,"OnReceive started");

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

            updateText(TAG);
            Log.d(TAG,"OnReceive ended");
        }
    }

    //method to updateText
    /*TODO make this method into the update method for the server/backend
    * log time and battery remaining to a file and send one pair to server?
    * then have the backup in case server doesn't receive update?*/
    void updateText(String TAG) {
        Log.d(TAG,"updateText started");

        //update BatteryRemainder and get the time and store them
        long updateTimeMillis = updateBatteryRemainderNanoWatt();

        updateText = "isCharging: " + isCharging +"\n"+
                "usbCharge: " + usbCharge +"\n"+
                "acCharge: " + acCharge +"\n"+
                "batteryPct: " + batteryPct +"\n"+
                "batteryRemainingNanoWatt: " + method() +"µW\n"+
                "updateTimeMillis: " + (!(updateTimeMillis == -1)? updateTimeMillis +"ms": "not updated");
        statusText.setText( updateText);
        Log.d(TAG,"updateText ended");
    }

    private long updateBatteryRemainderNanoWatt() {
        long updateBatteryRemainderNanoWatt = batteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);

        //if it is not supported on the device method will not store the value
        if (!(updateBatteryRemainderNanoWatt == Long.MIN_VALUE) ){
            batteryRemainingNanoWatt = updateBatteryRemainderNanoWatt;
            return System.currentTimeMillis();
        }
        //and return -1
        return -1;
    }

    double method() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            Toast.makeText(MainActivity.this, batteryCapacity + " mah",
                    Toast.LENGTH_LONG).show();
            return  batteryCapacity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    final int power_profile_id = com.android.internal.R.xml.power_profile;
}
