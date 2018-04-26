package com.example.itsme.richnoteclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import static android.support.v4.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static android.support.v4.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static android.support.v4.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

public class MainActivity extends AppCompatActivity {


    private final String ACTION_CONNECTION_CHANGED = ConnectivityManager.CONNECTIVITY_ACTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(myToolbar);
        initializeAndRegisterReceivers();
    }

    private void initializeAndRegisterReceivers() {

        //start the connectivity receiver and register for changes in connection
        ConnectionReceiver connectionReceiver = new ConnectionReceiver();
        registerReceiver(connectionReceiver, new IntentFilter(ACTION_CONNECTION_CHANGED));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
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
        return super.onOptionsItemSelected(item);
    }


    //class for looking at bandwidth
    private class ConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //connection changed, it could mean there is no internet or the type of connection has changed
            Bundle bundle = intent.getExtras();
        }
    }
    private void startSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }


    //Todo fill out actions based on network connection type
    private void conectivityCheck() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Checks if the device is on a metered network
        if (ConnectivityManagerCompat.isActiveNetworkMetered(connMgr)) {
            // Checks user’s Data Saver settings.
            switch (ConnectivityManagerCompat.getRestrictBackgroundStatus(connMgr)) {
                case RESTRICT_BACKGROUND_STATUS_ENABLED:
                    // Background data usage is blocked for this app. Wherever possible,
                    // the app should also use less data in the foreground.
                    /**Todo: ask the user if they want to open the settings app to whitelist this app on positiveResult launch intent request whitelist permissions by sending a Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS intent containing a URI of your app's package name: for example, package:MY_APP_ID */
                case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                    // The app is whitelisted. Wherever possible,
                    // the app should use less data in the foreground and background.

                case RESTRICT_BACKGROUND_STATUS_DISABLED:
                    // Data Saver is disabled. Since the device is connected to a
                    // metered network, the app should use less data wherever possible.

            }
        } else {
            // The device is not on a metered network.
            // Use data as required to perform syncs, downloads, and updates.
        }
    }
}
