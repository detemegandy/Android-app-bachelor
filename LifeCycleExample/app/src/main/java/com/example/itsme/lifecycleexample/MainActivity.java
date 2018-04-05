package com.example.itsme.lifecycleexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("onCreate","we are in onCreate" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.w("onStart","we are in onStart" );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.w("onResume","we are in onResume" );
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.w("onPause","we are in onPause" );
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.w("onStop","we are in onStop" );
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.w("onDestroy","we are in onDestroy" );
        super.onDestroy();
    }

}
