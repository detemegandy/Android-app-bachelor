package com.example.itsme.richnoteclient;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

import static android.support.v4.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.support.v4.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.support.v4.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

public class MainActivity extends AppCompatActivity {

    //request_code for startactivityforresult
    private static final int REQUEST_CODE = 22;

    //tag for logging
    String TAG = this.getClass().getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 123;
    private final String ACTION_CONNECTION_CHANGED = ConnectivityManager.CONNECTIVITY_ACTION;

    //used to tag update to the server
    private static String UPDATE_BACKEND = String.valueOf(R.string.update_backend);

    //SharedPreferences for persistent data
    SharedPreferences preferences;

    //TextView to guide user to setup notifikasjons
    TextView infoTextView;
    private boolean isLoggedInToSpotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(myToolbar);
        initClassFields();

        //ask the user for special permissions before using it in update backend in the callback for permission granted
        askForReadPhoneState();

    }

    private void askForReadPhoneState() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {
            // Permission has already been granted
            scheduleUpdateBackend();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        String info;
        Log.d(TAG, "ON REQUEST PERMISSION RESULT STARTED");

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    scheduleUpdateBackend();

                } else {
                    // permission denied, ask again
                    askForReadPhoneState();
                }
                return;
            }
        }
    }

    /*schedules the updateBackend worker every 30-60min
     * */
    private void scheduleUpdateBackend() {
        //needs to run once every 60 minutes
        long maxMinutes = 60;
        //and wait at least 30 minutes to start
        long minMinutes = 30;

        //needs internet connection and should not run when battery is low
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        final WorkManager workManager = WorkManager.getInstance();


        // make the workrequest with the constraints, timeunits are the same
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                UpdateBackEnd.class,
                maxMinutes, TimeUnit.MINUTES,
                minMinutes, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(UPDATE_BACKEND)
                .build();
        workManager.enqueue(workRequest);

    }


    private void initClassFields() {
        infoTextView = findViewById(R.id.info_textview);
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        isLoggedInToSpotify = preferences.getBoolean("isLoggedInToSpotify", false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem spotifyLoginItem = menu.findItem(R.id.action_spotify_login);
        if (isLoggedInToSpotify) {
            //already logged in, make login menu item invisible
            spotifyLoginItem.setVisible(false);
            //todo add logout item
        }
        return true;

    }

    //when the toolbar has an item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //if its the settings icon start the settingsactivity
        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }

        //if its the spotify login icon start the spotifyLoginActivity
        if (id == R.id.action_spotify_login) {
            startSpotifyLoginActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSpotifyLoginActivity() {
        Intent intent = new Intent(this, SpotifyLoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            isLoggedInToSpotify = data.getBooleanExtra("isLoggedInToSpotify", false);
            if (isLoggedInToSpotify) {
                //refresh the menu
                supportInvalidateOptionsMenu();
            }
        }
    }


    private void startSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
