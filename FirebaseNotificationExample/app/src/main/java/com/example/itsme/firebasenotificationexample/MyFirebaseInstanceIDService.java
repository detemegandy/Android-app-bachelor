package com.example.itsme.firebasenotificationexample;

import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static String registrationToken;

    @org.jetbrains.annotations.Contract(pure = true)
    static String getToken(){
        return registrationToken;
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        registrationToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + registrationToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(registrationToken);
    }

    private void sendRegistrationToServer(String registrationToken) {

        //create JSON to send with data
        JSONObject data = new JSONObject();
        try {
            data.put("msg_type", "new_user");
            data.put("firebase_token", registrationToken);

        } catch (JSONException e){
            e.printStackTrace();
        }
        String dataString = data.toString();

        //schedule job
        SendToBackend sendToBackend = new  SendToBackend();
        sendToBackend.doInBackground(dataString);
    }
}
