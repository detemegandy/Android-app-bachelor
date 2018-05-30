package com.example.itsme.richnoteclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncResult;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.work.Constraints;

import static android.security.KeyStore.getApplicationContext;


public class SendToBackendTask extends AsyncTask<String, String, String> {

    static final String TAG = "sendToBackendTask";
    String url = "http://35.204.23.172";
    String response = "";
    JSONObject jsonObject;
    Context context;

    public SendToBackendTask(Context context){
        //set context variables if required
        this.context = context;
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
            if (responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                handleResponse(response);




            }
            else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                response="BAD REQUEST";

            } else {
                response = "responsecode: " + responseCode;
            }
            out.close();

            urlConnection.disconnect();


        } catch (Exception e) {

            response = "NONE";
            e.printStackTrace();

        }
        Log.d(TAG,"doInBackGround ended, response: " + response);


        return urlString;
    }

    private void handleResponse(String response) {
        Log.d(TAG, "response: " + response);
        try {
            jsonObject = new JSONObject(response);
            String msg_type = jsonObject.getString("msg_type");
            switch (msg_type){
                case "spotify_url":
                    String spotify_url = jsonObject.getString("spotify_url");
                    if(spotify_url!=null) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(String.valueOf(R.string.spotify_login_link_key), spotify_url);
                        editor.commit();
                    }
                    break;
                case "kdfa":
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }
}
