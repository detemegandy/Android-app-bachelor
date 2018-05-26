package com.example.itsme.firebasenotificationexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MainActivity extends AppCompatActivity {

    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        //get the intent that started this activity from the notification
        Intent intent = getIntent();
        upDateText();
    }
    void upDateText() {
        textView.setText(MyFirebaseInstanceIDService.getToken());
    }

//    void onFirstRun(){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        boolean isFirstRun = prefs.getBoolean(getString(R.string.pref_is_first_run),false);
//        if(!isFirstRun){
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putBoolean(getString(R.string.pref_is_first_run), Boolean.TRUE);
//            editor.commit();
//            //get device unique id and send to back-end
//            initServer();
//        }
//    }
//
//    //
//    private void initServer() {
//        //get device UUID
//        int UUID = 123;
//
//        //create JSON to send with data
//        JSONObject data = new JSONObject();
//        try {
//            data.put("msg_type", "new_user");
//            data.put("id", UUID);
//
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//
//        String dataString = data.toString();
//        //schedule job
//        SendToBackend sendToBackend = new  SendToBackend();
//        sendToBackend.doInBackground(dataString);
//
//    }
}
