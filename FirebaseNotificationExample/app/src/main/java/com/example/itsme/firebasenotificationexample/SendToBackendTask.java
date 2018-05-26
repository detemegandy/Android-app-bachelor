package com.example.itsme.firebasenotificationexample;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendToBackendTask extends AsyncTask<String, String, String> {

    static final String TAG = "sendToBackendTask";
    String url = "http://35.204.23.172";
    String response = "";
    public SendToBackendTask(){
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG,"doInBackGround started");

        String urlString = url; // URL to call

        String data = params[0]; //data to post
        Log.d(TAG, "doInBackGround data: \n" + data);

        OutputStream out = null;
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);

            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            writer.write(data);

            writer.flush();

            writer.close();

            int responseCode = urlConnection.getResponseCode();


            //reads the JSON in the reply as a String
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                response="BAD REQUEST";

            } else {
                response = "responsecode: " + responseCode;
            }
            out.close();

            urlConnection.connect();


        } catch (Exception e) {

            response = "NONE";
            Log.e(TAG, e.getMessage());

        }
        Log.d(TAG,"doInBackGround ended, response: " + response);

        return urlString;
    }
}
