package com.example.itsme.richnoteclient;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.net.ConnectivityManagerCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import androidx.work.Worker;

import static java.lang.System.currentTimeMillis;

/**
 * Worker to update backend with current constraints.
 *
 */
public class UpdateBackEnd extends Worker {
    String TAG = this.getClass().getSimpleName();

    public UpdateBackEnd() {
    }

    @NonNull
    @Override
    public WorkerResult doWork() {
        //the data to send to server
        JSONObject data = new JSONObject();
        try {
            //tell the server the type of msg and device its coming from
            data.put("msg_type", "update_constraints");
            data.put("device_id", FirebaseInstanceId.getInstance().getToken());

            //if we could add the information to the data object
            if (gotBatteryInfo(data) && gotMobileDataInfo(data)) {
                //send the data to backend
                if (sendData(data)) {
                    Log.d(TAG, "successfully updated data :" + data);
                } else {
                    Log.d(TAG, "data was not sent" + data);
                }
            } else {
                return WorkerResult.RETRY;
            }//if else
        } catch (JSONException e) {
            e.printStackTrace();
            return WorkerResult.RETRY;
        }//try catch
        if (sendData(data))
        return WorkerResult.SUCCESS;
        return WorkerResult.RETRY;
    }//doWork

    /** sends data to backend
     *
     * @param data data sendt to backend
     * @return true always
     * TODO @return false if no success .
     */
    private Boolean sendData(JSONObject data) {
        SendToBackendTask task = new SendToBackendTask(getApplicationContext());
        task.doInBackground(data.toString());
        return true;
    }



    private Boolean gotMobileDataInfo(JSONObject data) {

        //fields that will hold info to put into data json
        Boolean isWifiConnected;
        long mobileDataUsage = 0;
        long dataLimit = 0;


        //connection info
        ConnectivityManager connmgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connmgr.getActiveNetworkInfo();
        isWifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;

        //usage info

        NetworkStats networkStats = null;
        //get the telephonymanager to query about data usage
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        //get a calendar with current month and set date to first
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startOfCurrentMonth = calendar.getTimeInMillis();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String subscriberId = telephonyManager.getSubscriberId();
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
            if (networkStatsManager != null) {
                try {
                    networkStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE, subscriberId, startOfCurrentMonth, currentTimeMillis() );
                } catch (RemoteException e) {
                    e.printStackTrace();
                }//catch
            }//if networkStatsmanager..
        }// if ActivityCompat..


        //usage constraint defined by user in MB and convert to byte
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dataLimit = 1048576 * Integer.valueOf(sharedPreferences.getString("mobile_data_limit", "0"));

        //if we got a result
        if(networkStats != null && networkStats.hasNextBucket()) {

            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            networkStats.getNextBucket(bucket);
            mobileDataUsage = bucket.getRxBytes() + bucket.getTxBytes();
        }

        //if got data, add info to outputdata
        if (mobileDataUsage != 0 && dataLimit != 0) {
            try {
                data.put("mobileDataUsage", mobileDataUsage);
                data.put("isWifiConnected", isWifiConnected);
                data.put("dataLimit", dataLimit);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //and return true
            return true;
        }


        return false;
    }

    private Boolean gotBatteryInfo(JSONObject data) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null,intentFilter);
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        // factors for batterypct
        float batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        float batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        //calculate batterypct
        float batteryPct = batteryLevel / batteryScale;

        try {
            data.put("isCharging", isCharging);
            data.put("usbCharge", usbCharge);
            data.put("acCharge", acCharge);
            data.put("batteryPct", batteryPct);
            Log.d(TAG, "gotBatteryInfo success data: " + data);
            //success if we got this far
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //if we get here no success
        return false;
    }
}
