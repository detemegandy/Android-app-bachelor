package com.example.itsme.richnoteclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**opens a webview which sends the user to a spotify login screen
 * to get Oauth tokens to server.
 * updates isLoggedInToSpotify in the sharedpreferences according to response from backend
 * **/
public class SpotifyLoginActivity extends AppCompatActivity {
    private WebView webView;
    String loginURL;
    SharedPreferences sharedPreferences;


    private boolean isLoggedInToSpotify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);

        Intent intent = getIntent();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loginURL = sharedPreferences.getString(String.valueOf(R.string.spotify_login_link_key), "about:blank");

        webView = findViewById(R.id.webview_spotify_login);

        //we need to enable javascript to give the server an easy way to interact with app
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        //used for debugging
        webView.setWebViewClient(new WebViewClient() {

//
//
//            @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            String TAG = "shouldOverrideUrlLoadin";
//            Uri url = request.getUrl();
//            Log.d(TAG, "loading URI : " + url);
//            return super.shouldOverrideUrlLoading(view, request);
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            String TAG = "onPageFinished";
//            Log.d(TAG, "finished loading url: " + url);
//            super.onPageFinished(view, url);
//        }
    });


        if (!isLoggedInToSpotify)
        webView.loadUrl(loginURL);
    }

    // add methods the server can use in the page loaded from server
    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * get reply from server with login status
         */
        @JavascriptInterface
        public void sendClientReply(String reply) {
            String TAG = "sendClientReply";
            Log.d(TAG, "reply: " + reply);
            switch (reply) {
                case "SUCCESS":
                    isLoggedInToSpotify = true;
                    setResult(RESULT_OK, new Intent().putExtra("isLoggedInToSpotify", isLoggedInToSpotify));
                    break;
                case "FAILURE":
                    isLoggedInToSpotify = false;
                    break;
                default:
                    isLoggedInToSpotify = false;
            }
            Log.d(TAG, "isLoggedInToSpotify: " + isLoggedInToSpotify);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedInToSpotify", isLoggedInToSpotify);
            //needs to be written immediately so the mainactivity can use it
            editor.commit();
            finish();
        }
    }


}
